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

import ChooseModal from '@/pages/Other/Login/ChooseModal';
import { gotoRedirectUrl, initSomeThing, redirectToLogin } from '@/pages/Other/Login/function';
import LangSwitch from '@/pages/Other/Login/LangSwitch';
import { chooseTenantSubmit, login, queryDataByParams } from '@/services/BusinessCrud';
import { API } from '@/services/data';
import { API_CONSTANTS } from '@/services/endpoints';
import { SaTokenInfo, UserBaseInfo } from '@/types/AuthCenter/data.d';
import { setTenantStorageAndCookie } from '@/utils/function';
import { useLocalStorage } from '@/utils/hook/useLocalStorage';
import { l } from '@/utils/intl';
import { ErrorMessage, SuccessMessageAsync } from '@/utils/messages';
import { useEmotionCss } from '@ant-design/use-emotion-css';
import { useModel } from '@umijs/max';
import React, { useEffect, useState } from 'react';
import HelmetTitle from './HelmetTitle';
import LoginForm from './LoginForm';
import { TOKEN_KEY } from '@/services/constants';

const Login: React.FC = () => {
  const [submitting, setSubmitting] = useState(false);
  const { initialState, setInitialState } = useModel('@@initialState');
  const [tenantVisible, handleTenantVisible] = useState<boolean>(false);
  const [tenant, setTenant] = useState<UserBaseInfo.Tenant[]>([]);

  const [localStorageOfToken, setLocalStorageOfToken] = useLocalStorage(TOKEN_KEY, '');

  const containerClassName = useEmotionCss(() => {
    return {
      display: 'flex',
      flexDirection: 'column',
      height: '100%'
    };
  });

  const fetchUserInfo = async () => {
    const userInfo = await initialState?.fetchUserInfo?.();
    if (userInfo) {
      setInitialState((s) => ({
        ...s,
        currentUser: userInfo
      }));
    }
  };

  useEffect(() => {
    initSomeThing();
  }, []);

  /**
   * When the token is expired, redirect to login
   */
  useEffect(() => {
    const expirationTime =
      (JSON.parse(JSON.stringify(localStorageOfToken)) as SaTokenInfo)?.tokenTimeout ?? 0; // GET TOKEN TIMEOUT
    let timeRemaining = 0;
    let timer: NodeJS.Timeout;
    if (expirationTime > 0) {
      //  calculate the time difference
      const currentTime = Date.now();
      timeRemaining = expirationTime - currentTime;
      //  use setInterval to set a timer
      timer = setInterval(() => redirectToLogin(l('login.token.error')), timeRemaining);
    }
    return () => {
      clearTimeout(timer);
    };
  }, [initialState, localStorageOfToken]);

  const handleChooseTenant = async (chooseTenantResult: API.Result) => {
    if (chooseTenantResult.code === 0) {
      await SuccessMessageAsync(
        l('login.chooseTenantSuccess', '', {
          msg: chooseTenantResult.msg,
          tenantCode: chooseTenantResult.data.tenantCode
        })
      );
      /**
       * After the selection is complete, refresh all user information
       */
      await fetchUserInfo();

      /**
       * Redirect to home page && reconnect Global Sse
       */
      gotoRedirectUrl();
    } else {
      ErrorMessage(l('login.chooseTenantFailed'));
      return;
    }
  };

  /**
   * Determine whether the tenant list is empty
   * @param tenantList
   */
  const assertTenant = async (tenantList: UserBaseInfo.Tenant[]) => {
    if (tenantList === null || tenantList.length === 0) {
      ErrorMessage(l('login.notbindtenant'));
      return;
    } else {
      setTenant(tenantList);
    }
  };

  /**
   * when login user has only one tenant ,directly login to the system
   * @param tenantList
   */
  const singleTenant = async (tenantList: UserBaseInfo.Tenant[]) => {
    const tenantId = tenantList[0].id;
    setTenantStorageAndCookie(tenantId);
    const chooseTenantResult: API.Result = await chooseTenantSubmit({
      tenantId
    });

    await handleChooseTenant(chooseTenantResult);
  };

  const handleSubmitLogin = async (values: API.LoginParams) => {
    try {
      // login
      const result = await login({ ...values });

      if (result.code === 0) {
        // if login success then get token info and set it to local storage
        await queryDataByParams<SaTokenInfo>(API_CONSTANTS.TOKEN_INFO).then((res) => {
          if (res) {
            setLocalStorageOfToken(JSON.stringify(res));
          } else {
            // 如果没有获取到token信息，直接跳转到登录页
            redirectToLogin(l('login.token.error'));
          }
        });
      }
      setInitialState((s) => ({ ...s, currentUser: result.data }));
      await SuccessMessageAsync(l('login.result', '', { msg: result.msg, time: result.time }));
      /**
       * After successful login, set the tenant list
       */
      const tenantList: UserBaseInfo.Tenant[] = result.data.tenantList;
      await assertTenant(tenantList);
      /**
       * Determine whether the current tenant list is multiple
       * 1. If there are multiple execution pop-up modals, the user selects a specific tenant to enter the system
       * 2. If it is a single, only use the unique tenant id to enter the system directly
       */
      if (tenantList && tenantList.length > 1) {
        handleTenantVisible(true);
      } else {
        await singleTenant(tenantList);
      }
      return;
    } catch (error: any) {
      return;
    }
  };

  /**
   * Confirm the tenant selection of login users
   * @param tenantId
   */
  const handleConfirmChooseTenant = async (tenantId: number) => {
    setSubmitting(true);
    const result = await chooseTenantSubmit({ tenantId: tenantId });
    await handleChooseTenant(result);
    handleTenantVisible(false);
  };

  return (
    <div className={containerClassName}>
      <HelmetTitle />
      <LangSwitch />
      <LoginForm onSubmit={handleSubmitLogin} />
      <ChooseModal
        tenantVisible={tenantVisible}
        handleTenantVisible={() => handleTenantVisible(false)}
        submitting={submitting}
        handleChooseTenant={(tenantId) => handleConfirmChooseTenant(tenantId)}
        tenant={tenant}
      />
    </div>
  );
};

export default Login;
