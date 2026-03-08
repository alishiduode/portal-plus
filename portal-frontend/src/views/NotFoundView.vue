<template>
  <div class="notfound-page">
    <div class="notfound-content">
      <div class="notfound-code">404</div>
      <h2>页面不存在</h2>
      <p>您访问的页面可能已被删除或地址有误</p>
      <el-button type="primary" size="large" round @click="$router.push('/')">
        <el-icon><House /></el-icon>
        返回首页
      </el-button>
    </div>
  </div>
</template>

<style scoped>
.notfound-page {
  padding-top: 64px;
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
}

.notfound-content {
  text-align: center;
  padding: 40px;
}

.notfound-code {
  font-size: 120px;
  font-weight: 900;
  background: var(--gradient-primary);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  line-height: 1;
  margin-bottom: 16px;
}

h2 {
  font-size: 24px;
  color: var(--text-primary);
  margin-bottom: 12px;
}

p {
  color: var(--text-muted);
  margin-bottom: 32px;
}
</style>
