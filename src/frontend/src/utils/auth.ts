import Cookies from 'js-cookie'

const TokenKey = 'security-teaching-token'
const RefreshTokenKey = 'security-teaching-refresh-token'

export function getToken(): string {
  // 优先从Cookie获取，然后从localStorage，最后从sessionStorage
  return Cookies.get(TokenKey) || localStorage.getItem(TokenKey) || sessionStorage.getItem(TokenKey) || ''
}

export function setToken(token: string, rememberMe: boolean = false): void {
  if (rememberMe) {
    // 记住我：设置到Cookie（30天过期）
    Cookies.set(TokenKey, token, { expires: 30 })
    // 同时设置到localStorage作为备份（永久存储）
    localStorage.setItem(TokenKey, token)
  } else {
    // 不记住我：设置到Cookie（会话级，浏览器关闭后失效）
    Cookies.set(TokenKey, token)
    // 同时设置到sessionStorage（会话级存储）
    sessionStorage.setItem(TokenKey, token)
    // 清除localStorage中的token（如果存在）
    localStorage.removeItem(TokenKey)
  }
}

export function removeToken(): void {
  Cookies.remove(TokenKey)
  localStorage.removeItem(TokenKey)
  sessionStorage.removeItem(TokenKey)
}

export function getRefreshToken(): string {
  return Cookies.get(RefreshTokenKey) || localStorage.getItem(RefreshTokenKey) || ''
}

export function setRefreshToken(token: string): void {
  Cookies.set(RefreshTokenKey, token, { expires: 30 })
  localStorage.setItem(RefreshTokenKey, token)
}

export function removeRefreshToken(): void {
  Cookies.remove(RefreshTokenKey)
  localStorage.removeItem(RefreshTokenKey)
}

export function clearAuth(): void {
  removeToken()
  removeRefreshToken()
  localStorage.removeItem('user-info')
  localStorage.removeItem('user-roles')
  localStorage.removeItem('user-permissions')
}
