<template>
  <el-scrollbar class="code-block">
    <pre><code ref="codeRef" :class="codeClass">{{ displayContent }}</code></pre>
  </el-scrollbar>
</template>

<script setup lang="ts">
import { computed, nextTick, onMounted, ref, watch } from 'vue'
import hljs from 'highlight.js'

const props = defineProps({
  content: {
    type: String,
    default: ''
  },
  language: {
    type: String,
    default: 'plaintext'
  }
})

const codeRef = ref<HTMLElement | null>(null)

const displayContent = computed(() => props.content || '')
const codeClass = computed(() => `language-${props.language || 'plaintext'}`)

const highlight = () => {
  if (codeRef.value) {
    hljs.highlightElement(codeRef.value)
  }
}

onMounted(() => {
  highlight()
})

watch(
  () => props.content,
  () => {
    nextTick(() => highlight())
  }
)
</script>

<style scoped lang="scss">
.code-block {
  max-height: 260px;
  background: var(--el-color-info-light-9);
  border: 1px solid var(--el-border-color-lighter);
  border-radius: 6px;
  padding: 8px;
  font-size: 12px;
  line-height: 1.5;

  pre {
    margin: 0;
    white-space: pre-wrap;
    word-break: break-word;
  }
}
</style>
