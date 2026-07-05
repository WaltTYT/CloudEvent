<script setup>
import { ref, onMounted, onBeforeUnmount, nextTick } from 'vue'
import * as echarts from 'echarts'

const chartRef = ref(null)
let chartInstance = null

onMounted(async () => {
  await nextTick()
  chartInstance = echarts.init(chartRef.value)
  chartInstance.setOption({
    title: { text: '文章数据概览', left: 'center' },
    tooltip: { trigger: 'axis' },
    legend: { data: ['发布量'], bottom: 0 },
    xAxis: {
      type: 'category',
      data: ['1月', '2月', '3月', '4月', '5月', '6月']
    },
    yAxis: { type: 'value' },
    series: [{
      name: '发布量',
      type: 'bar',
      data: [23, 45, 38, 56, 42, 68],
      itemStyle: { color: '#409eff', borderRadius: [4, 4, 0, 0] }
    }],
    grid: { left: '3%', right: '3%', bottom: '15%', containLabel: true }
  })
  window.addEventListener('resize', handleResize)
})

const handleResize = () => {
  chartInstance?.resize()
}

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  chartInstance?.dispose()
})
</script>

<template>
  <div class="chart-page">
    <el-card>
      <div ref="chartRef" style="height:500px"></div>
    </el-card>
  </div>
</template>

<style scoped>
.chart-page {
  max-width: 900px;
  margin: 0 auto;
}
</style>
