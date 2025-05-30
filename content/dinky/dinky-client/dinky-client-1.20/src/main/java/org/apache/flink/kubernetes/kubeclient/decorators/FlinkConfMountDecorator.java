/*
 *
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package org.apache.flink.kubernetes.kubeclient.decorators;

import static org.apache.flink.kubernetes.utils.Constants.CONFIG_MAP_PREFIX;
import static org.apache.flink.kubernetes.utils.Constants.FLINK_CONF_VOLUME;
import static org.apache.flink.util.Preconditions.checkNotNull;
import static org.dinky.constant.FlinkParamConstant.CONFIG_FILE_NAME_LIST;

import org.apache.flink.annotation.VisibleForTesting;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.ConfigurationUtils;
import org.apache.flink.configuration.DeploymentOptionsInternal;
import org.apache.flink.configuration.GlobalConfiguration;
import org.apache.flink.configuration.JobManagerOptions;
import org.apache.flink.configuration.RestOptions;
import org.apache.flink.configuration.TaskManagerOptions;
import org.apache.flink.kubernetes.configuration.KubernetesConfigOptions;
import org.apache.flink.kubernetes.kubeclient.FlinkPod;
import org.apache.flink.kubernetes.kubeclient.parameters.AbstractKubernetesParameters;
import org.apache.flink.kubernetes.shaded.io.fabric8.kubernetes.api.model.ConfigMap;
import org.apache.flink.kubernetes.shaded.io.fabric8.kubernetes.api.model.ConfigMapBuilder;
import org.apache.flink.kubernetes.shaded.io.fabric8.kubernetes.api.model.Container;
import org.apache.flink.kubernetes.shaded.io.fabric8.kubernetes.api.model.ContainerBuilder;
import org.apache.flink.kubernetes.shaded.io.fabric8.kubernetes.api.model.HasMetadata;
import org.apache.flink.kubernetes.shaded.io.fabric8.kubernetes.api.model.KeyToPath;
import org.apache.flink.kubernetes.shaded.io.fabric8.kubernetes.api.model.KeyToPathBuilder;
import org.apache.flink.kubernetes.shaded.io.fabric8.kubernetes.api.model.Pod;
import org.apache.flink.kubernetes.shaded.io.fabric8.kubernetes.api.model.PodBuilder;
import org.apache.flink.kubernetes.shaded.io.fabric8.kubernetes.api.model.Volume;
import org.apache.flink.kubernetes.shaded.io.fabric8.kubernetes.api.model.VolumeBuilder;
import org.apache.flink.kubernetes.utils.Constants;
import org.apache.flink.shaded.guava31.com.google.common.io.Files;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Mounts the log4j.properties, logback.xml, and config.yaml configuration on the JobManager or
 * TaskManager pod.
 */
public class FlinkConfMountDecorator extends AbstractKubernetesStepDecorator {

    private final AbstractKubernetesParameters kubernetesComponentConf;

    public FlinkConfMountDecorator(AbstractKubernetesParameters kubernetesComponentConf) {
        this.kubernetesComponentConf = checkNotNull(kubernetesComponentConf);
    }

    @Override
    public FlinkPod decorateFlinkPod(FlinkPod flinkPod) {
        final Pod mountedPod = decoratePod(flinkPod.getPodWithoutMainContainer());

        final Container mountedMainContainer = new ContainerBuilder(flinkPod.getMainContainer())
                .addNewVolumeMount()
                .withName(FLINK_CONF_VOLUME)
                .withMountPath(kubernetesComponentConf.getFlinkConfDirInPod())
                .endVolumeMount()
                .build();

        return new FlinkPod.Builder(flinkPod)
                .withPod(mountedPod)
                .withMainContainer(mountedMainContainer)
                .build();
    }

    private Pod decoratePod(Pod pod) {
        final List<KeyToPath> keyToPaths = getLocalLogConfFiles().stream()
                .map(file -> new KeyToPathBuilder()
                        .withKey(file.getName())
                        .withPath(file.getName())
                        .build())
                .collect(Collectors.toList());
        keyToPaths.add(new KeyToPathBuilder()
                .withKey(GlobalConfiguration.getFlinkConfFilename())
                .withPath(GlobalConfiguration.getFlinkConfFilename())
                .build());

        final Volume flinkConfVolume = new VolumeBuilder()
                .withName(FLINK_CONF_VOLUME)
                .withNewConfigMap()
                .withName(getFlinkConfConfigMapName(kubernetesComponentConf.getClusterId()))
                .withItems(keyToPaths)
                .endConfigMap()
                .build();

        return new PodBuilder(pod)
                .editSpec()
                .addNewVolumeLike(flinkConfVolume)
                .endVolume()
                .endSpec()
                .build();
    }

    @Override
    public List<HasMetadata> buildAccompanyingKubernetesResources() throws IOException {
        final String clusterId = kubernetesComponentConf.getClusterId();

        final Map<String, String> data = new HashMap<>();

        final List<File> localLogFiles = getLocalLogConfFiles();
        for (File file : localLogFiles) {
            data.put(file.getName(), Files.toString(file, StandardCharsets.UTF_8));
        }

        final List<String> confData = getClusterSideConfData(kubernetesComponentConf.getFlinkConfiguration());
        data.put(GlobalConfiguration.getFlinkConfFilename(), getFlinkConfData(confData));

        final ConfigMap flinkConfConfigMap = new ConfigMapBuilder()
                .withApiVersion(Constants.API_VERSION)
                .withNewMetadata()
                .withName(getFlinkConfConfigMapName(clusterId))
                .withLabels(kubernetesComponentConf.getCommonLabels())
                .endMetadata()
                .addToData(data)
                .build();

        return Collections.singletonList(flinkConfConfigMap);
    }

    /** Get properties map for the cluster-side after removal of some keys. */
    private List<String> getClusterSideConfData(Configuration flinkConfig) {
        final Configuration clusterSideConfig = flinkConfig.clone();
        // Remove some configuration options that should not be taken to cluster side.
        clusterSideConfig.removeConfig(KubernetesConfigOptions.KUBE_CONFIG_FILE);
        clusterSideConfig.removeConfig(DeploymentOptionsInternal.CONF_DIR);
        clusterSideConfig.removeConfig(RestOptions.BIND_ADDRESS);
        clusterSideConfig.removeConfig(JobManagerOptions.BIND_HOST);
        clusterSideConfig.removeConfig(TaskManagerOptions.BIND_HOST);
        clusterSideConfig.removeConfig(TaskManagerOptions.HOST);
        return ConfigurationUtils.convertConfigToWritableLines(clusterSideConfig, true);
    }

    @VisibleForTesting
    String getFlinkConfData(List<String> confData) throws IOException {
        try (StringWriter sw = new StringWriter();
                PrintWriter out = new PrintWriter(sw)) {
            confData.forEach(out::println);

            return sw.toString();
        }
    }

    private List<File> getLocalLogConfFiles() {
        final String confDir = kubernetesComponentConf.getConfigDirectory();

        List<File> localLogConfFiles = new ArrayList<>();
        for (String fileName : CONFIG_FILE_NAME_LIST) {
            final File file = new File(confDir, fileName);
            if (file.exists()) {
                localLogConfFiles.add(file);
            }
        }

        return localLogConfFiles;
    }

    @VisibleForTesting
    public static String getFlinkConfConfigMapName(String clusterId) {
        return CONFIG_MAP_PREFIX + clusterId;
    }
}
