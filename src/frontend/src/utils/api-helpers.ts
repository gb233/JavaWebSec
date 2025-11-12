export interface ApiResponseFlags {
  code?: number | string
  success?: boolean
  error?: boolean
}

const normalizeCode = (code?: number | string): number | undefined => {
  if (typeof code === 'number') {
    return code
  }
  if (typeof code === 'string') {
    const trimmed = code.trim()
    if (!trimmed.length) {
      return undefined
    }
    const numeric = Number(trimmed)
    return Number.isNaN(numeric) ? undefined : numeric
  }
  return undefined
}

export const isSuccessResponse = (res?: ApiResponseFlags | null): boolean => {
  if (!res) {
    return false
  }

  if (res.success === true) {
    return true
  }

  if (res.success === false) {
    return false
  }

  const normalizedCode = normalizeCode(res.code)
  if (normalizedCode !== undefined) {
    return normalizedCode === 200
  }

  if (res.error === false) {
    return true
  }

  return false
}
