// @ts-ignore
/* eslint-disable */
import request from '@/request'

/** 此处后端没有提供注释 GET /point/account */
export async function getMyAccount(options?: { [key: string]: any }) {
  return request<API.BaseResponseUserAccountVO>('/point/account', {
    method: 'GET',
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 POST /point/accounts */
export async function listAccounts(
  body: API.UserAccountQueryRequest,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponsePageUserAccountVO>('/point/accounts', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 POST /point/invitation/handle */
export async function handleInvitationCode(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.handleInvitationCodeParams,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponseVoid>('/point/invitation/handle', {
    method: 'POST',
    params: {
      ...params,
    },
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 GET /point/invitation/my-code */
export async function getMyInvitationCode(options?: { [key: string]: any }) {
  return request<API.BaseResponseString>('/point/invitation/my-code', {
    method: 'GET',
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 POST /point/logs */
export async function getLogs(body: API.PointLogQueryRequest, options?: { [key: string]: any }) {
  return request<API.BaseResponsePagePointLogVO>('/point/logs', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 POST /point/my-logs */
export async function getMyLogs(body: API.PointLogQueryRequest, options?: { [key: string]: any }) {
  return request<API.BaseResponsePagePointLogVO>('/point/my-logs', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 PUT /point/rules */
export async function updateRule(
  body: API.PointRuleUpdateRequest,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponseBoolean>('/point/rules', {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 GET /point/rules/all */
export async function getAllRules(options?: { [key: string]: any }) {
  return request<API.BaseResponseListPointRuleVO>('/point/rules/all', {
    method: 'GET',
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 POST /point/rules/grant */
export async function grantPoints(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.grantPointsParams,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponseBoolean>('/point/rules/grant', {
    method: 'POST',
    params: {
      ...params,
    },
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 POST /point/rules/grant-all */
export async function grantPointsToAll(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.grantPointsToAllParams,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponseInteger>('/point/rules/grant-all', {
    method: 'POST',
    params: {
      ...params,
    },
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 POST /point/sign-in */
export async function signIn(options?: { [key: string]: any }) {
  return request<API.BaseResponsePointSignInResponse>('/point/sign-in', {
    method: 'POST',
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 GET /point/sign-status */
export async function getSignStatus(options?: { [key: string]: any }) {
  return request<API.BaseResponseBoolean>('/point/sign-status', {
    method: 'GET',
    ...(options || {}),
  })
}
