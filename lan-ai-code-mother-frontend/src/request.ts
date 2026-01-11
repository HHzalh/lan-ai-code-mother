import axios from 'axios'
import { message } from 'ant-design-vue'
import { API_BASE_URL } from '@/config/env'

// 参数序列化器：将大数字转换为字符串，避免精度丢失
const paramsSerializer = (params: any) => {
  if (!params) return ''
  const result: any = {}
  for (const key in params) {
    const value = params[key]
    // 如果是数字类型且超过安全整数范围，转换为字符串
    if (typeof value === 'number' && value > Number.MAX_SAFE_INTEGER) {
      result[key] = String(value)
    } else if (value !== null && value !== undefined) {
      result[key] = value
    }
  }
  return new URLSearchParams(result).toString()
}

// 处理大数字精度丢失：将对象中的大数字转换为字符串
const convertBigNumbersToString = (obj: any): any => {
  if (obj === null || obj === undefined) {
    return obj
  }
  if (Array.isArray(obj)) {
    return obj.map(convertBigNumbersToString)
  }
  if (typeof obj === 'object') {
    const result: any = {}
    for (const key in obj) {
      const value = obj[key]
      // 如果是数字类型且超过安全整数范围，转换为字符串
      if (typeof value === 'number' && value > Number.MAX_SAFE_INTEGER) {
        result[key] = String(value)
      } else if (typeof value === 'object') {
        result[key] = convertBigNumbersToString(value)
      } else {
        result[key] = value
      }
    }
    return result
  }
  return obj
}

// 创建 Axios 实例
const myAxios = axios.create({
  baseURL: API_BASE_URL,
  timeout: 60000,
  withCredentials: true,
  paramsSerializer: paramsSerializer,
})

// 全局请求拦截器
myAxios.interceptors.request.use(
  function (config) {
    // FormData 不需要处理，直接跳过
    if (config.data instanceof FormData) {
      return config
    }
    // 处理 POST/PUT 请求 body 中的大数字精度丢失问题
    if (config.data && typeof config.data === 'object') {
      config.data = convertBigNumbersToString(config.data)
    }
    return config
  },
  function (error) {
    // Do something with request error
    return Promise.reject(error)
  },
)

// 全局响应拦截器
myAxios.interceptors.response.use(
  function (response) {
    const { data } = response
    // 未登录
    if (data.code === 40100) {
      // 不是获取用户信息的请求，并且用户目前不是已经在用户登录页面，则跳转到登录页面
      if (
        !response.request.responseURL.includes('user/get/login') &&
        !window.location.pathname.includes('/user/login')
      ) {
        message.warning('请先登录')
        window.location.href = `/user/login?redirect=${window.location.href}`
      }
    }
    return response
  },
  function (error) {
    // Any status codes that falls outside the range of 2xx cause this function to trigger
    // Do something with response error
    return Promise.reject(error)
  },
)

export default myAxios
