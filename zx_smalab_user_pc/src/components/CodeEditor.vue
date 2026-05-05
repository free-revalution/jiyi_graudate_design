<template>
  <div class="code-editor-wrapper">
    <MonacoEditor
      v-model:value="code"
      :language="language"
      :theme="theme"
      :options="mergedOptions"
      :height="height"
      @change="handleChange"
      @mount="handleMount"
    />
  </div>
</template>

<script setup>
import { ref, computed, watch, defineAsyncComponent } from 'vue';
import { Loader } from '@element-plus/icons-vue';

const MonacoEditor = defineAsyncComponent(() => import('monaco-editor-vue3'));

const props = defineProps({
  modelValue: {
    type: String,
    default: ''
  },
  language: {
    type: String,
    default: 'javascript'
  },
  theme: {
    type: String,
    default: 'vs-dark'
  },
  height: {
    type: String,
    default: '400px'
  },
  options: {
    type: Object,
    default: () => ({})
  },
  readOnly: {
    type: Boolean,
    default: false
  }
});

const emit = defineEmits(['update:modelValue', 'change', 'mount']);

const code = ref(props.modelValue);

// 合并默认配置和用户配置
const mergedOptions = computed(() => ({
  fontSize: 14,
  minimap: { enabled: true },
  scrollBeyondLastLine: false,
  wordWrap: 'on',
  lineNumbers: 'on',
  renderWhitespace: 'selection',
  tabSize: 2,
  automaticLayout: true,
  readOnly: props.readOnly,
  ...props.options
}));

// 监听外部 modelValue 变化
watch(() => props.modelValue, (newVal) => {
  if (newVal !== code.value) {
    code.value = newVal;
  }
});

// 监听内部 code 变化
watch(code, (newVal) => {
  emit('update:modelValue', newVal);
});

// 处理代码变化
const handleChange = (value) => {
  emit('change', value);
};

// 编辑器挂载完成
const handleMount = (editor) => {
  emit('mount', editor);
};
</script>

<style lang="scss" scoped>
.code-editor-wrapper {
  width: 100%;
  height: 100%;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  overflow: hidden;
}
</style>
