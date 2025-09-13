'use client'

import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from 'recharts'

interface TrafficData {
  visits: number
  rank: number
  bounce: number
  avgDuration: number
  countries: Array<{ country: string, share: number, visits: number }>
  devices: Array<{ device: string, share: number }>
  referrers: Array<{ site: string, share: number }>
  searchShare: number
  socialShare: number
}

interface TrafficChartProps {
  data: TrafficData
}

export function TrafficChart({ data }: TrafficChartProps) {
  const chartData = [
    { name: 'Visits', value: data.visits },
    { name: 'Bounce Rate', value: data.bounce },
    { name: 'Duration (min)', value: Math.round(data.avgDuration / 60) },
    { name: 'Search Share', value: data.searchShare },
    { name: 'Social Share', value: data.socialShare },
  ]

  return (
    <div className="bg-white dark:bg-gray-800 rounded-lg p-6 shadow-sm border border-gray-200 dark:border-gray-700">
      <h3 className="text-lg font-semibold text-gray-900 dark:text-white mb-4">
        Traffic Overview
      </h3>
      <div className="h-64">
        <ResponsiveContainer width="100%" height="100%">
          <BarChart data={chartData}>
            <CartesianGrid strokeDasharray="3 3" />
            <XAxis dataKey="name" />
            <YAxis />
            <Tooltip />
            <Bar dataKey="value" fill="#3B82F6" />
          </BarChart>
        </ResponsiveContainer>
      </div>
    </div>
  )
}
