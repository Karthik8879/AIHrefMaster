'use client';

import { motion } from 'framer-motion';
import { LineChart, Line, ResponsiveContainer, Area, AreaChart } from 'recharts';

interface SparkLineProps {
  data: { value: number; timestamp: string }[];
  title: string;
  value: number;
  unit?: string;
  color?: string;
  className?: string;
}

export default function SparkLine({ 
  data, 
  title, 
  value, 
  unit = '', 
  color = '#3B82F6',
  className = '' 
}: SparkLineProps) {
  return (
    <motion.div
      initial={{ opacity: 0, y: 20 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.6 }}
      className={`bg-white dark:bg-gray-800 rounded-lg shadow-md p-6 border border-gray-200 dark:border-gray-700 ${className}`}
    >
      <div className="flex items-center justify-between mb-4">
        <h3 className="text-lg font-semibold text-gray-900 dark:text-white">
          {title}
        </h3>
        <div className="text-right">
          <div className="text-2xl font-bold text-gray-900 dark:text-white">
            {value}
            {unit && <span className="text-sm text-gray-500 dark:text-gray-400 ml-1">{unit}</span>}
          </div>
        </div>
      </div>
      
      <div className="h-20">
        <ResponsiveContainer width="100%" height="100%">
          <AreaChart data={data}>
            <defs>
              <linearGradient id={`gradient-${title}`} x1="0" y1="0" x2="0" y2="1">
                <stop offset="5%" stopColor={color} stopOpacity={0.3}/>
                <stop offset="95%" stopColor={color} stopOpacity={0}/>
              </linearGradient>
            </defs>
            <Area
              type="monotone"
              dataKey="value"
              stroke={color}
              strokeWidth={2}
              fill={`url(#gradient-${title})`}
            />
          </AreaChart>
        </ResponsiveContainer>
      </div>
    </motion.div>
  );
}
