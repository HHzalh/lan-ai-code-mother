<template>
  <a-layout-footer class="footer">
    <div class="footer-content">
      <p class="copyright">
        <a
          class="author-link"
          href="https://www.gitee.com/hhzalh"
          rel="noopener noreferrer"
          target="_blank"
        >
          © 2025 by hhzalh
        </a>
      </p>
    </div>
  </a-layout-footer>
</template>

<script lang="ts" setup>
// 无需额外的响应式数据
</script>

<style scoped>
.footer {
  background: #efefef;
  text-align: center;
  padding: 20px;
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
}

.copyright {
  margin: 0;
  color: #666;
  font-size: 14px;
}
</style>
