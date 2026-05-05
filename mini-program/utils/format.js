function splitTags(tags) {
  if (!tags) return ['源头直发']
  return String(tags).split(',').map(s => s.trim()).filter(Boolean).slice(0, 4)
}

function formatPrice(price, unit) {
  if (price === null || price === undefined || price === '') return '面议'
  const n = Number(price)
  if (Number.isNaN(n)) return String(price)
  return '¥' + n.toFixed(2) + (unit ? '/' + String(unit).replace(/^元\//, '') : '')
}

function normalizeArticle(item, api) {
  if (!item) return item
  return Object.assign({}, item, {
    coverImage: api.normalizeImage(item.coverImage),
    tagsArray: splitTags(item.productTags),
    priceText: formatPrice(item.price, item.unit),
    originText: item.originPlace || item.categoryName || '本地农户',
    farmerText: item.farmerName || '本地农户',
    phoneText: item.farmerPhone || '请联系服务站'
  })
}

function normalizeArticles(list, api) {
  return (list || []).map(item => normalizeArticle(item, api))
}

function contentToLines(content) {
  if (!content) return ['暂无详细介绍']
  return String(content)
    .replace(/<[^>]+>/g, '')
    .replace(/[#>*`_\-]/g, '')
    .split(/\n+/)
    .map(s => s.trim())
    .filter(Boolean)
}

module.exports = {
  splitTags,
  formatPrice,
  normalizeArticle,
  normalizeArticles,
  contentToLines
}
