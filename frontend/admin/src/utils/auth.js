export function getToken() {
  return localStorage.getItem('token')
}

export function setToken(token) {
  localStorage.setItem('token', token)
}

export function removeToken() {
  localStorage.removeItem('token')
}

export function getUserInfo() {
  const info = localStorage.getItem('userInfo')
  return info ? JSON.parse(info) : null
}

export function setUserInfo(info) {
  localStorage.setItem('userInfo', JSON.stringify(info))
}

export function removeUserInfo() {
  localStorage.removeItem('userInfo')
}

export function getPermissions() {
  const permissions = localStorage.getItem('permissions')
  return permissions ? JSON.parse(permissions) : []
}

export function setPermissions(permissions) {
  localStorage.setItem('permissions', JSON.stringify(permissions))
}

export function removePermissions() {
  localStorage.removeItem('permissions')
}

export function clearAuth() {
  removeToken()
  removeUserInfo()
  removePermissions()
}

export function hasPermission(code) {
  const permissions = getPermissions()
  return permissions.includes(code)
}
