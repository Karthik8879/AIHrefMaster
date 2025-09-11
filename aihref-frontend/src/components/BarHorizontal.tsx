'use client';

import { motion } from 'framer-motion';

interface BarData {
  name: string;
  value: number;
  color?: string;
}

interface BarHorizontalProps {
  data: BarData[];
  title: string;
  maxValue?: number;
  className?: string;
}

export default function BarHorizontal({ 
  data, 
  title, 
  maxValue, 
  className = '' 
}: BarHorizontalProps) {
  const max = maxValue || Math.max(...data.map(d => d.value));

  return (
    <motion.div
      initial={{ opacity: 0, y: 20 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.6 }}
      className={`bg-white dark:bg-gray-800 rounded-lg shadow-md p-6 border border-gray-200 dark:border-gray-700 ${className}`}
    >
      <h3 className="text-lg font-semibold text-gray-900 dark:text-white mb-4">
        {title}
      </h3>
      <div className="space-y-3">
        {data.map((item, index) => (
          <motion.div
            key={item.name}
            initial={{ opacity: 0, x: -20 }}
            animate={{ opacity: 1, x: 0 }}
            transition={{ duration: 0.5, delay: index * 0.1 }}
            className="flex items-center space-x-3"
          >
            <div className="w-20 text-sm text-gray-600 dark:text-gray-400 truncate">
              {item.name}
            </div>
            <div className="flex-1 bg-gray-200 dark:bg-gray-700 rounded-full h-2 relative overflow-hidden">
              <motion.div
                className={`h-full rounded-full ${
                  item.color || 'bg-blue-500'
                }`}
                initial={{ width: 0 }}
                animate={{ width: `${(item.value / max) * 100}%` }}
                transition={{ duration: 0.8, delay: index * 0.1 + 0.3 }}
              />
            </div>
            <div className="w-16 text-sm text-gray-900 dark:text-white text-right">
              {item.value.toLocaleString()}
            </div>
          </motion.div>
        ))}
      </div>
    </motion.div>
  );
}
