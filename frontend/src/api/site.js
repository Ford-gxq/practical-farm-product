import request from './request'

export const getSiteCategories = () => request.get('/site/categories')
export const getSiteArticles = params => request.get('/site/articles', { params })
export const getSiteArticleDetail = id => request.get(`/site/articles/${id}`)
