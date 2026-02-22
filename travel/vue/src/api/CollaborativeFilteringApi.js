import request from '@/utils/request'

/**
 * 协同过滤推荐API
 * 
 * @module CollaborativeFilteringApi
 */

/**
 * 获取协同过滤推荐景点列表
 * 
 * 算法说明：
 * - 基于用户的协同过滤：找到相似用户，推荐他们喜欢的景点
 * - 基于物品的协同过滤：找到相似景点，推荐用户可能喜欢的景点
 * - 混合策略：结合两种算法的结果
 * 
 * @param {object} params - 请求参数
 * @param {number} params.userId - 用户ID
 * @param {number} params.topN - 推荐数量（默认10）
 * @param {object} callbacks - 回调函数
 * @param {Function} callbacks.onSuccess - 成功回调，参数为推荐景点列表
 * @param {Function} callbacks.onError - 错误回调
 * @returns {Promise}
 */
export function getCollaborativeFilteringRecommendations(params, callbacks) {
  return request.get('/recommendation/collaborative-filtering/recommend', params, callbacks)
}

/**
 * 获取推荐景点ID列表（轻量级）
 * 
 * 适合需要轻量级响应的场景
 * 
 * @param {object} params - 请求参数
 * @param {number} params.userId - 用户ID
 * @param {number} params.topN - 推荐数量（默认10）
 * @param {object} callbacks - 回调函数
 * @param {Function} callbacks.onSuccess - 成功回调，参数为推荐景点ID列表
 * @param {Function} callbacks.onError - 错误回调
 * @returns {Promise}
 */
export function getCollaborativeFilteringRecommendationIds(params, callbacks) {
  return request.get('/recommendation/collaborative-filtering/recommend-ids', params, callbacks)
}
