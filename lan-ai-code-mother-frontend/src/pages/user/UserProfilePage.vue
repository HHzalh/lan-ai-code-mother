<script lang="ts" setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { message, type UploadProps } from 'ant-design-vue'
import { UploadOutlined } from '@ant-design/icons-vue'
import { useLoginUserStore } from '@/stores/loginUser'
import { updateUser, uploadUserAvatar } from '@/api/userController'

const router = useRouter()
const loginUserStore = useLoginUserStore()
const submitting = ref(false)
const avatarUploading = ref(false)

const formState = reactive<Partial<API.UserUpdateRequest>>({
  id: undefined,
  userAccount: '',
  userName: '',
  userProfile: '',
  userAvatar: '',
})

const displayAvatar = computed(() => {
  return formState.userAvatar
})

const initForm = async () => {
  if (!loginUserStore.loginUser.id) {
    await loginUserStore.fetchLoginUser()
  }
  const user = loginUserStore.loginUser
  if (!user?.id) {
    message.warning('请先登录')
    router.push('/user/login')
    return
  }
  Object.assign(formState, {
    id: user.id,
    userAccount: user.userAccount,
    userName: user.userName,
    userProfile: user.userProfile,
    userAvatar: user.userAvatar,
  })
}

onMounted(() => {
  initForm()
})

const toBase64 = (file: File) =>
  new Promise<string>((resolve, reject) => {
    const reader = new FileReader()
    reader.readAsDataURL(file)
    reader.onload = () => resolve(reader.result as string)
    reader.onerror = (error) => reject(error)
  })

const handleAvatarUpload: UploadProps['beforeUpload'] = async (file) => {
  avatarUploading.value = true
  try {
    const formData = new FormData()
    formData.append('file', file)
    const res = await uploadUserAvatar(
      { file: '' },
      {
        data: formData,
        params: undefined,
      },
    )
    if (res.data.code === 0 && res.data.data) {
      formState.userAvatar = res.data.data
      message.success('头像已上传')
      await loginUserStore.fetchLoginUser()
    } else {
      message.error(res.data.message ?? '上传失败，请重试')
    }
  } catch (error) {
    message.error('上传失败，请重试')
  } finally {
    avatarUploading.value = false
  }
  return false
}

const handleSubmit = async () => {
  if (!formState.id) {
    message.error('缺少用户 ID，无法更新')
    return
  }
  submitting.value = true
  try {
    const res = await updateUser(formState as API.UserUpdateRequest)
    if (res.data.code === 0) {
      message.success('资料已更新')
      await loginUserStore.fetchLoginUser()
    } else {
      message.error(res.data.message ?? '更新失败')
    }
  } finally {
    submitting.value = false
  }
}
</script>

<template>
  <div class="profile-wrapper">
    <section class="profile-hero">
      <div class="hero-content">
        <p class="eyebrow">PROFILE</p>
        <h2>个人资料中心</h2>
        <p class="subtitle">让世界看到更棒的你</p>
      </div>
      <div class="hero-divider"></div>
      <div class="hero-avatar">
        <a-avatar :size="96" :src="displayAvatar" />
        <p class="avatar-tip">{{ formState.userName || '无名' }}</p>
      </div>
    </section>

    <section class="profile-card">
      <div class="card-header">
        <span class="line"></span>
        <h3>基础信息</h3>
      </div>
      <a-form
        :label-col="{ span: 5 }"
        :model="formState"
        :wrapper-col="{ span: 19 }"
        label-align="left"
        @finish="handleSubmit"
      >
        <a-form-item label="账号">
          <a-input v-model:value="formState.userAccount" disabled />
        </a-form-item>
        <a-form-item
          :rules="[{ required: true, message: '请输入昵称' }]"
          label="昵称"
          name="userName"
        >
          <a-input v-model:value="formState.userName" placeholder="请输入昵称" />
        </a-form-item>
        <a-form-item label="头像">
          <div class="avatar-uploader">
            <a-avatar :size="80" :src="displayAvatar" />
            <a-upload
              :before-upload="handleAvatarUpload"
              :show-upload-list="false"
              accept="image/*"
            >
              <a-button :loading="avatarUploading" type="default">
                <UploadOutlined />
                重新上传
              </a-button>
            </a-upload>
          </div>
        </a-form-item>
        <a-form-item label="个人简介" name="userProfile">
          <a-textarea
            v-model:value="formState.userProfile"
            :rows="4"
            placeholder="介绍一下自己，内容会显示在个人名片中"
          />
        </a-form-item>
        <a-form-item :wrapper-col="{ span: 19, offset: 5 }" class="form-actions">
          <a-button :loading="submitting" html-type="submit" type="primary">保存修改</a-button>
        </a-form-item>
      </a-form>
    </section>
  </div>
</template>

<style scoped>
.profile-wrapper {
  max-width: 960px;
  margin: 0 auto;
  padding: 24px 0 64px;
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.profile-hero {
  background: linear-gradient(120deg, #e0f2ff, #f5f7ff);
  border-radius: 18px;
  padding: 28px 32px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.05);
  border: 1px solid rgba(24, 144, 255, 0.15);
}

.hero-content {
  flex: 1;
}

.eyebrow {
  letter-spacing: 0.4em;
  font-size: 12px;
  color: #3c92ff;
  margin-bottom: 8px;
}

.profile-hero h2 {
  margin: 0;
  font-size: 28px;
  color: #1f2d3d;
}

.subtitle {
  margin-top: 8px;
  color: #5f6b7c;
}

.hero-divider {
  width: 1px;
  height: 80px;
  background: rgba(255, 255, 255, 0.7);
  margin: 0 32px;
}

.hero-avatar {
  text-align: center;
}

.avatar-tip {
  margin-top: 8px;
  color: #4f5969;
}

.profile-card {
  background: #fff;
  border-radius: 18px;
  padding: 32px 40px 40px;
  box-shadow: 0 12px 35px rgba(15, 39, 80, 0.07);
  border: 1px solid #f0f2f5;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 32px;
}

.card-header h3 {
  margin: 0;
  position: relative;
  padding-bottom: 8px;
}

.card-header h3::after {
  content: '';
  position: absolute;
  left: 0;
  bottom: 0;
  width: 100%;
  height: 3px;
  background: linear-gradient(90deg, #1890ff, #6bc1ff);
  border-radius: 2px;
}

.card-header .line {
  display: none;
}

.avatar-uploader {
  display: flex;
  align-items: center;
  gap: 16px;
}

.form-actions {
  margin-top: 24px;
}

@media (max-width: 768px) {
  .profile-hero {
    flex-direction: column;
    gap: 16px;
  }

  .hero-divider {
    width: 60%;
    height: 1px;
    margin: 12px 0;
  }

  .profile-card {
    padding: 24px;
  }
}
</style>
