<template>
  <div class="cf-recommendation-container">
    <!-- 标题区域 -->
    <div class="header-section">
      <h3 class="section-title">
        <i class="fas fa-brain"></i>
        为您推荐
      </h3>
    </div>

    <!-- 空状态 -->
    <div class="empty-state">
      <div class="empty-icon">
        <i class="fas fa-inbox"></i>
      </div>
      <p class="empty-text">暂无推荐</p>
      <p class="empty-hint">推荐功能开发中，敬请期待</p>
    </div>

  </div>
</template>

<script setup>
</script>

<style lang="scss" scoped>
.cf-recommendation-container {
  background: white;
  border-radius: 12px;
  padding: 24px 32px;

  margin-top: 24px;
}

// 标题区域
.header-section {
  margin-bottom: 24px;

  .section-title {
    font-size: 18px;
    font-weight: 700;
    color: #1f2937;
    margin: 0;
    display: flex;
    align-items: center;
    gap: 8px;

    i {
      color: #8b5cf6;
      font-size: 20px;
    }
  }
}

// 景点卡片网格（与精选景点相同）
.scenic-cards-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 24px;
}

// 推荐卡片（与精选景点相同）
.scenic-item-card {
  background: white;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);

  &:hover {
    transform: translateY(-8px);
    box-shadow: 0 12px 24px rgba(0, 0, 0, 0.15);

    .scenic-card-image {
      transform: scale(1.1);
    }
  }
}

.scenic-card-image {
  height: 300px;
  overflow: hidden;
  position: relative;
  background: #f3f4f6 center / cover no-repeat;
  background-size: cover;
  background-position: center;
  transition: transform 0.5s ease;
}

.scenic-price-badge {
  position: absolute;
  top: 12px;
  left: 12px;
  background: linear-gradient(135deg, #06b6d4, #0891b2);
  color: white;
  padding: 6px 12px;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 600;
  z-index: 2;

  &.price-tag {
    background: rgba(255, 255, 255, 0.95);
    color: #1f2937;
  }
}

.scenic-rating-badge {
  position: absolute;
  top: 12px;
  right: 12px;
  background: rgba(0, 0, 0, 0.7);
  color: white;
  padding: 6px 12px;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 4px;
  z-index: 2;

  i {
    color: #fbbf24;
  }
}

.scenic-card-body {
  padding: 20px;
}

.scenic-card-name {
  font-size: 20px;
  font-weight: 700;
  color: #1f2937;
  margin: 0 0 12px 0;
  line-height: 1.3;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.scenic-card-location {
  font-size: 14px;
  color: #6b7280;
  margin: 0 0 12px 0;
  display: flex;
  align-items: center;
  gap: 6px;

  i {
    color: #3b82f6;
  }
}

.scenic-card-desc {
  font-size: 14px;
  color: #6b7280;
  line-height: 1.6;
  margin: 0;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

// 加载状态
.loading-state {
  padding: 24px;
  background: #f8f9fa;
  border-radius: 12px;
  border: 2px dashed #e5e7eb;

  .loading-text {
    text-align: center;
    color: #8b5cf6;
    font-size: 16px;
    margin-top: 16px;
    font-weight: 600;
  }
}

// 空状态
.empty-state {
  text-align: center;
  padding: 48px 24px;

  .empty-icon {
    font-size: 48px;
    color: #d1d5db;
    margin-bottom: 16px;
  }

  .empty-text {
    font-size: 18px;
    font-weight: 600;
    color: #6b7280;
    margin: 0 0 8px 0;
  }

  .empty-hint {
    font-size: 14px;
    color: #9ca3af;
    margin: 0;
  }
}

// 响应式样式
@media (max-width: 1024px) {
  .scenic-cards-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (max-width: 768px) {
  .cf-recommendation-container {
    padding: 20px;
  }

  .scenic-cards-grid {
    grid-template-columns: 1fr;
    gap: 16px;
  }

  .header-section {
    margin-bottom: 20px;

    .section-title {
      font-size: 20px;
    }
  }
}
</style>