<template>
  <footer class="app-footer">
    <div class="footer-inner">
      <div class="footer-left">
        <div class="footer-logo">
          <el-icon><Monitor /></el-icon>
          Dev<span>Portal</span>
        </div>
        <p class="footer-desc">个人开发者作品集 · 展示每一个精心打磨的项目</p>
      </div>
      <div class="footer-right">
        <span class="footer-copy">© {{ year }} DevPortal. 用代码创造价值</span>
      </div>
    </div>
  </footer>
</template>

<script setup>
const year = new Date().getFullYear()
</script>

<style scoped>
.app-footer {
  background: var(--bg-secondary);
  border-top: 1px solid var(--border-color);
  padding: 24px 0;
  margin-top: auto;
}

.footer-inner {
  max-width: 1280px;
  margin: 0 auto;
  padding: 0 24px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: 16px;
}

.footer-logo {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 18px;
  font-weight: 700;
  color: var(--text-primary);
  margin-bottom: 8px;
}

.footer-logo span {
  color: var(--primary-light);
}

.footer-desc {
  color: var(--text-muted);
  font-size: 13px;
}

.footer-copy {
  color: var(--text-muted);
  font-size: 13px;
}
</style>
