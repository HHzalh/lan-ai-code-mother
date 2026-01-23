<template>
  <div class="user-info">
    <a-avatar :size="size" :src="user?.userAvatar">
      {{ user?.userName?.charAt(0) || 'U' }}
    </a-avatar>
    <a-tooltip v-if="showName && user?.userName" :title="user.userName">
      <span class="user-name">{{ truncateText(user.userName, 12) }}</span>
    </a-tooltip>
    <span v-else-if="showName" class="user-name">未知用户</span>
  </div>
</template>

<script lang="ts" setup>
interface Props {
  user?: API.UserVO
  size?: number | 'small' | 'default' | 'large'
  showName?: boolean
}

withDefaults(defineProps<Props>(), {
  size: 'default',
  showName: true,
})

// 截取文本
const truncateText = (text: string, maxLength: number) => {
  if (!text) return ''
  if (text.length <= maxLength) return text
  return text.substring(0, maxLength) + '...'
}
</script>

<style scoped>
.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.user-name {
  font-size: 14px;
  color: #1a1a1a;
}
</style>
