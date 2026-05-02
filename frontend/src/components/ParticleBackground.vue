<template>
  <!--
    粒子背景画布
    fixed：固定在浏览器窗口
    inset: 0：铺满整个屏幕
    z-index: 0：放在登录卡片下面
  -->
  <canvas ref="canvasRef" class="particle-canvas"></canvas>
</template>

<script setup>
import { onMounted, onBeforeUnmount, ref } from 'vue'

/**
 * 获取 canvas DOM 元素
 */
const canvasRef = ref(null)

/**
 * 画布上下文
 */
let ctx = null

/**
 * 粒子数组
 */
let particles = []

/**
 * 动画 ID，用于组件销毁时取消动画
 */
let animationId = null

/**
 * 粒子数量
 * 数字越大，粒子越密集，但性能消耗也越高
 */
const PARTICLE_COUNT = 80

/**
 * 粒子最大连线距离
 */
const LINE_DISTANCE = 140

/**
 * 鼠标位置
 */
const mouse = {
  x: null,
  y: null,
  radius: 160
}

/**
 * 初始化 canvas 尺寸
 */
function resizeCanvas() {
  const canvas = canvasRef.value
  if (!canvas) return

  canvas.width = window.innerWidth
  canvas.height = window.innerHeight
}

/**
 * 创建单个粒子
 */
function createParticle() {
  const canvas = canvasRef.value

  return {
    // 粒子的 x 坐标
    x: Math.random() * canvas.width,

    // 粒子的 y 坐标
    y: Math.random() * canvas.height,

    // 粒子的半径
    radius: Math.random() * 2 + 1,

    // x 方向速度
    speedX: (Math.random() - 0.5) * 0.8,

    // y 方向速度
    speedY: (Math.random() - 0.5) * 0.8
  }
}

/**
 * 初始化所有粒子
 */
function initParticles() {
  particles = []

  for (let i = 0; i < PARTICLE_COUNT; i++) {
    particles.push(createParticle())
  }
}

/**
 * 绘制单个粒子
 */
function drawParticle(particle) {
  ctx.beginPath()
  ctx.arc(particle.x, particle.y, particle.radius, 0, Math.PI * 2)
  ctx.fillStyle = 'rgba(255, 255, 255, 0.85)'
  ctx.fill()
}

/**
 * 更新粒子位置
 */
function updateParticle(particle) {
  const canvas = canvasRef.value

  particle.x += particle.speedX
  particle.y += particle.speedY

  // 碰到左右边界反弹
  if (particle.x < 0 || particle.x > canvas.width) {
    particle.speedX *= -1
  }

  // 碰到上下边界反弹
  if (particle.y < 0 || particle.y > canvas.height) {
    particle.speedY *= -1
  }

  // 鼠标靠近时，粒子轻微远离鼠标
  if (mouse.x !== null && mouse.y !== null) {
    const dx = particle.x - mouse.x
    const dy = particle.y - mouse.y
    const distance = Math.sqrt(dx * dx + dy * dy)

    if (distance < mouse.radius) {
      const force = (mouse.radius - distance) / mouse.radius
      particle.x += (dx / distance) * force * 1.5
      particle.y += (dy / distance) * force * 1.5
    }
  }
}

/**
 * 绘制粒子之间的连线
 */
function drawLines() {
  for (let i = 0; i < particles.length; i++) {
    for (let j = i + 1; j < particles.length; j++) {
      const p1 = particles[i]
      const p2 = particles[j]

      const dx = p1.x - p2.x
      const dy = p1.y - p2.y
      const distance = Math.sqrt(dx * dx + dy * dy)

      if (distance < LINE_DISTANCE) {
        const opacity = 1 - distance / LINE_DISTANCE

        ctx.beginPath()
        ctx.moveTo(p1.x, p1.y)
        ctx.lineTo(p2.x, p2.y)
        ctx.strokeStyle = `rgba(100, 180, 255, ${opacity * 0.35})`
        ctx.lineWidth = 1
        ctx.stroke()
      }
    }
  }
}

/**
 * 动画循环
 */
function animate() {
  const canvas = canvasRef.value
  if (!canvas) return

  ctx.clearRect(0, 0, canvas.width, canvas.height)

  particles.forEach((particle) => {
    updateParticle(particle)
    drawParticle(particle)
  })

  drawLines()

  animationId = requestAnimationFrame(animate)
}

/**
 * 监听鼠标移动
 */
function handleMouseMove(event) {
  mouse.x = event.clientX
  mouse.y = event.clientY
}

/**
 * 鼠标离开窗口时，取消鼠标影响
 */
function handleMouseLeave() {
  mouse.x = null
  mouse.y = null
}

/**
 * 监听窗口尺寸变化
 */
function handleResize() {
  resizeCanvas()
  initParticles()
}

onMounted(() => {
  const canvas = canvasRef.value
  ctx = canvas.getContext('2d')

  resizeCanvas()
  initParticles()
  animate()

  window.addEventListener('resize', handleResize)
  window.addEventListener('mousemove', handleMouseMove)
  window.addEventListener('mouseleave', handleMouseLeave)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  window.removeEventListener('mousemove', handleMouseMove)
  window.removeEventListener('mouseleave', handleMouseLeave)

  if (animationId) {
    cancelAnimationFrame(animationId)
  }
})
</script>

<style scoped>
.particle-canvas {
  position: fixed;
  left: 0;
  top: 0;
  width: 100%;
  height: 100%;
  z-index: 0;
  pointer-events: none;
}
</style>