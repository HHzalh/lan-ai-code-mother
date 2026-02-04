<template>
  <div class="captcha-row">
    <a-form-item
      :rules="[{ validator: validateCaptcha }]"
      :validateTrigger="['onBlur', 'submit']"
      name="captcha"
    >
      <a-input
        v-model:value="inputValue"
        class="captcha-input-field"
        placeholder="请输入验证码"
        size="large"
      >
        <template #prefix>
          <SafetyOutlined class="input-icon" />
        </template>
      </a-input>
    </a-form-item>

    <div class="captcha-canvas-wrapper" @click="refreshCaptcha">
      <canvas ref="canvasRef" class="captcha-canvas" height="50" width="120"></canvas>
      <div class="refresh-hint">点击刷新</div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { onMounted, ref, watch } from 'vue'
import { SafetyOutlined } from '@ant-design/icons-vue'

interface Props {
  modelValue?: string
}

interface Emits {
  (e: 'update:modelValue', value: string): void
}

const props = defineProps<Props>()
const emit = defineEmits<Emits>()

const canvasRef = ref<HTMLCanvasElement>()
const inputValue = ref(props.modelValue || '')
const captchaCode = ref('')

// 监听外部值变化
watch(
  () => props.modelValue,
  (newVal) => {
    inputValue.value = newVal || ''
  },
)

// 监听内部值变化
watch(inputValue, (newVal) => {
  emit('update:modelValue', newVal)
})

/**
 * 生成随机颜色
 */
const randomColor = (min: number, max: number) => {
  const r = Math.floor(Math.random() * (max - min) + min)
  const g = Math.floor(Math.random() * (max - min) + min)
  const b = Math.floor(Math.random() * (max - min) + min)
  return `rgb(${r},${g},${b})`
}

/**
 * 生成验证码
 */
const generateCaptchaCode = () => {
  const chars = 'ABCDEFGHJKMNPQRSTUVWXYZabcdefghjkmnpqrstuvwxyz23456789'
  let str = ''
  for (let i = 0; i < 4; i++) {
    str += chars[Math.floor(Math.random() * chars.length)]
  }
  return str
}

/**
 * 绘制验证码
 */
const drawCaptcha = () => {
  const canvas = canvasRef.value
  if (!canvas) return

  const ctx = canvas.getContext('2d')
  if (!ctx) return

  const width = 120
  const height = 50

  // 清空画布
  ctx.clearRect(0, 0, width, height)

  // 绘制背景
  ctx.fillStyle = randomColor(240, 255)
  ctx.fillRect(0, 0, width, height)

  // 生成验证码
  captchaCode.value = generateCaptchaCode()

  // 绘制文字
  for (let i = 0; i < 4; i++) {
    const fontSize = Math.floor(Math.random() * 8 + 24)
    const deg = Math.floor(Math.random() * 30 - 15)

    ctx.font = `bold ${fontSize}px Arial`
    ctx.textBaseline = 'middle'
    ctx.textAlign = 'center'

    ctx.translate(30 * i + 15, height / 2)
    ctx.rotate((deg * Math.PI) / 180)

    ctx.fillStyle = randomColor(50, 160)
    ctx.fillText(captchaCode.value[i], 0, 0)

    ctx.setTransform(1, 0, 0, 1, 0, 0)
  }

  // 绘制干扰线
  for (let i = 0; i < 5; i++) {
    ctx.beginPath()
    ctx.moveTo(Math.random() * width, Math.random() * height)
    ctx.lineTo(Math.random() * width, Math.random() * height)
    ctx.strokeStyle = randomColor(180, 230)
    ctx.lineWidth = 1
    ctx.stroke()
  }

  // 绘制干扰点
  for (let i = 0; i < 30; i++) {
    ctx.beginPath()
    ctx.arc(Math.random() * width, Math.random() * height, 1, 0, 2 * Math.PI)
    ctx.fillStyle = randomColor(150, 200)
    ctx.fill()
  }
}

/**
 * 刷新验证码
 */
const refreshCaptcha = () => {
  drawCaptcha()
  inputValue.value = ''
}

/**
 * 验证验证码
 */
const validateCaptcha = (_rule: unknown, value: string, callback: (error?: Error) => void) => {
  if (!value) {
    callback(new Error('请输入验证码'))
    return
  }
  // 只在输入长度达到4位时才验证
  if (value.length < 4) {
    callback(new Error('请输入完整的4位验证码'))
    return
  }
  if (value.toLowerCase() !== captchaCode.value.toLowerCase()) {
    callback(new Error('验证码错误，请重新输入'))
    // 不立即刷新，等用户再次点击刷新按钮时才刷新
  } else {
    callback()
  }
}

onMounted(() => {
  drawCaptcha()
})

// 暴露方法给父组件
defineExpose({
  refresh: refreshCaptcha,
  validate: () => inputValue.value.toLowerCase() === captchaCode.value.toLowerCase(),
})
</script>

<style scoped>
.captcha-row {
  display: flex;
  gap: 12px;
  align-items: flex-start;
}

.captcha-input-field {
  flex: 1;
}

.captcha-input-field :deep(.ant-input) {
  border-radius: 12px;
  height: 50px;
  font-size: 15px;
  padding-left: 44px;
  border: 2px solid #e5e7eb;
  background: #f9fafb;
  transition: all 0.3s ease;
}

.captcha-input-field :deep(.ant-input:hover) {
  border-color: #667eea;
  background: #ffffff;
}

.captcha-input-field :deep(.ant-input:focus),
.captcha-input-field :deep(.ant-input-focused) {
  border-color: #667eea;
  background: #ffffff;
  box-shadow: 0 0 0 4px rgba(102, 126, 234, 0.1);
}

.input-icon {
  color: #9ca3af;
  font-size: 16px;
  transition: color 0.3s ease;
}

.captcha-input-field:focus-within .input-icon {
  color: #667eea;
}

.captcha-canvas-wrapper {
  position: relative;
  width: 120px;
  height: 50px;
  flex-shrink: 0;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  border: 2px solid #e5e7eb;
  background: #ffffff;
  transition: all 0.3s ease;
}

.captcha-canvas-wrapper:hover {
  border-color: #667eea;
  transform: scale(1.02);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.2);
}

.captcha-canvas {
  width: 100%;
  height: 100%;
  display: block;
}

.refresh-hint {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background: rgba(0, 0, 0, 0.6);
  color: white;
  font-size: 10px;
  text-align: center;
  padding: 2px 0;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.captcha-canvas-wrapper:hover .refresh-hint {
  opacity: 1;
}

@media (max-width: 640px) {
  .captcha-row {
    flex-direction: column;
    gap: 8px;
  }

  .captcha-canvas-wrapper {
    width: 100%;
  }
}
</style>
