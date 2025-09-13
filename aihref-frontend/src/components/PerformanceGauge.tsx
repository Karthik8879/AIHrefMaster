'use client'

import { PieChart, Pie, Cell, ResponsiveContainer, Tooltip } from 'recharts'

interface WebVitals {
  lcp: number
  fid: number
  cls: number
  score: number
  screenshotB64: string | null
}

interface PerformanceGaugeProps {
  data: WebVitals
}

export function PerformanceGauge({ data }: PerformanceGaugeProps) {
  const chartData = [
    { name: 'LCP', value: data.lcp, color: '#3B82F6' },
    { name: 'FID', value: data.fid, color: '#10B981' },
    { name: 'CLS', value: data.cls, color: '#F59E0B' },
    { name: 'Score', value: data.score, color: '#EF4444' },
  ]

  const getScoreColor = (score: number) => {
    if (score >= 90) return 'text-green-600 dark:text-green-400'
    if (score >= 50) return 'text-yellow-600 dark:text-yellow-400'
    return 'text-red-600 dark:text-red-400'
  }

  return (
    <div className="bg-white dark:bg-gray-800 rounded-lg p-6 shadow-sm border border-gray-200 dark:border-gray-700">
      <h3 className="text-lg font-semibold text-gray-900 dark:text-white mb-4">
        Performance Metrics
      </h3>
      <div className="h-64">
        <ResponsiveContainer width="100%" height="100%">
          <PieChart>
            <Pie
              data={chartData}
              cx="50%"
              cy="50%"
              innerRadius={60}
              outerRadius={100}
              paddingAngle={5}
              dataKey="value"
            >
              {chartData.map((entry, index) => (
                <Cell key={`cell-${index}`} fill={entry.color} />
              ))}
            </Pie>
            <Tooltip />
          </PieChart>
        </ResponsiveContainer>
      </div>
      <div className="grid grid-cols-2 gap-4 mt-4">
        {chartData.map((item) => (
          <div key={item.name} className="flex items-center justify-between">
            <div className="flex items-center space-x-2">
              <div
                className="w-3 h-3 rounded-full"
                style={{ backgroundColor: item.color }}
              />
              <span className="text-sm text-gray-600 dark:text-gray-400">
                {item.name}
              </span>
            </div>
            <span className={`text-sm font-medium ${getScoreColor(item.value)}`}>
              {item.value}
            </span>
          </div>
        ))}
      </div>
    </div>
  )
}
