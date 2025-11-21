import { ref, onMounted, onUnmounted } from 'vue'

interface WebSocketMessage {
  type: string
  data: Record<string, unknown>
}

export function useWebSocket(url: string) {
  const socket = ref<WebSocket | null>(null)
  const connected = ref(false)
  const error = ref<string | null>(null)
  const messageHandlers = new Map<string, (data: Record<string, unknown>) => void>()

  const connect = () => {
    try {
      socket.value = new WebSocket(url)

      socket.value.onopen = () => {
        connected.value = true
        error.value = null
        console.log('WebSocket连接已建立')
      }

      socket.value.onmessage = event => {
        try {
          const message: WebSocketMessage = JSON.parse(event.data)
          const handler = messageHandlers.get(message.type)
          if (handler) {
            handler(message.data)
          }
        } catch (err) {
          console.error('解析WebSocket消息失败:', err)
        }
      }

      socket.value.onclose = () => {
        connected.value = false
        console.log('WebSocket连接已关闭')
        // 自动重连
        setTimeout(() => {
          if (!connected.value) {
            connect()
          }
        }, 3000)
      }

      socket.value.onerror = err => {
        error.value = 'WebSocket连接错误'
        console.error('WebSocket错误:', err)
      }
    } catch (err) {
      error.value = '无法创建WebSocket连接'
      console.error('WebSocket创建失败:', err)
    }
  }

  const disconnect = () => {
    if (socket.value) {
      socket.value.close()
      socket.value = null
      connected.value = false
    }
  }

  const send = (type: string, data: Record<string, unknown>) => {
    if (socket.value && connected.value) {
      const message = { type, data }
      socket.value.send(JSON.stringify(message))
    } else {
      console.warn('WebSocket未连接，无法发送消息')
    }
  }

  const on = (type: string, handler: (data: Record<string, unknown>) => void) => {
    messageHandlers.set(type, handler)
  }

  const off = (type: string) => {
    messageHandlers.delete(type)
  }

  onMounted(() => {
    connect()
  })

  onUnmounted(() => {
    disconnect()
  })

  return {
    connected,
    error,
    connect,
    disconnect,
    send,
    on,
    off
  }
}
