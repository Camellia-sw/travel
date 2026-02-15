<template>
  <div class="home-container">
    <!-- Hero静态背景区 -->
    <div class="hero-section">
      <div class="hero-background">
        <img src="https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=1920&h=500&fit=crop"
             alt="Hero Background" class="hero-image">
        <div class="hero-overlay"></div>
      </div>
      <div class="hero-content">
        <h1 class="hero-title">发现世界之美</h1>
        <p class="hero-subtitle">探索精彩旅程，创造美好回忆</p>
      </div>
    </div>
  </div>
</template>

<script setup>

</script>
<style lang="scss" scoped>
/* 全局容器样式 */
.home-container {
  width: 100%;
  background: #f8f9fa;
  min-height: 100vh;
}

/* Hero静态背景区域 */
.hero-section {
  position: relative;
  height: 500px;
  overflow: hidden;
}

.hero-background {
  position: absolute;
  inset: 0;
}

.hero-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.hero-overlay {
  position: absolute;
  inset: 0;
  background: linear-gradient(to right, rgba(0, 0, 0, 0.5), transparent);
}

.hero-content {
  position: relative;
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: flex-start;
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
  z-index: 2;
}

.hero-title {
  font-size: 48px;
  font-weight: 700;
  color: white;
  margin: 0 0 16px 0;
  max-width: 800px;
  animation: fadeInUp 1s ease-out;
}

.hero-subtitle {
  font-size: 20px;
  color: white;
  margin: 0;
  opacity: 0.9;
  animation: fadeInUp 1.2s ease-out;
}
</style>
