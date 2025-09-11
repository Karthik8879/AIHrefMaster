'use client';

import { motion } from 'framer-motion';

interface RadialGaugeProps {
  value: number;
  max?: number;
  label: string;
  unit?: string;
  color?: string;
  size?: number;
  strokeWidth?: number;
  className?: string;
}

export default function RadialGauge({
  value,
  max = 100,
  label,
  unit = '',
  color = '#3B82F6',
  size = 120,
  strokeWidth = 8,
  className = ''
}: RadialGaugeProps) {
  const radius = (size - strokeWidth) / 2;
  const circumference = 2 * Math.PI * radius;
  const normalizedValue = Math.min(Math.max(value, 0), max);
  const strokeDasharray = circumference;
  const strokeDashoffset = circumference - (normalizedValue / max) * circumference;

  const getScoreColor = (score: number) => {
    if (score >= 90) return '#10B981'; // green
    if (score >= 70) return '#F59E0B'; // yellow
    if (score >= 50) return '#F97316'; // orange
    return '#EF4444'; // red
  };

  const gaugeColor = getScoreColor(normalizedValue);

  return (
    <motion.div
      initial={{ opacity: 0, scale: 0.8 }}
      animate={{ opacity: 1, scale: 1 }}
      transition={{ duration: 0.6, delay: 0.2 }}
      className={`flex flex-col items-center ${className}`}
    >
      <div className="relative" style={{ width: size, height: size }}>
        <svg
          width={size}
          height={size}
          className="transform -rotate-90"
        >
          {/* Background circle */}
          <circle
            cx={size / 2}
            cy={size / 2}
            r={radius}
            stroke="currentColor"
            strokeWidth={strokeWidth}
            fill="none"
            className="text-gray-200 dark:text-gray-700"
          />
          {/* Progress circle */}
          <motion.circle
            cx={size / 2}
            cy={size / 2}
            r={radius}
            stroke={gaugeColor}
            strokeWidth={strokeWidth}
            fill="none"
            strokeLinecap="round"
            strokeDasharray={strokeDasharray}
            initial={{ strokeDashoffset: circumference }}
            animate={{ strokeDashoffset }}
            transition={{ duration: 1, ease: "easeInOut" }}
          />
        </svg>
        {/* Value text */}
        <div className="absolute inset-0 flex items-center justify-center">
          <div className="text-center">
            <motion.div
              initial={{ opacity: 0, y: 10 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ duration: 0.8, delay: 0.5 }}
              className="text-2xl font-bold text-gray-900 dark:text-white"
            >
              {normalizedValue.toFixed(1)}
              {unit && <span className="text-sm text-gray-500 dark:text-gray-400 ml-1">{unit}</span>}
            </motion.div>
          </div>
        </div>
      </div>
      <motion.p
        initial={{ opacity: 0 }}
        animate={{ opacity: 1 }}
        transition={{ duration: 0.6, delay: 0.8 }}
        className="text-sm font-medium text-gray-600 dark:text-gray-400 mt-2 text-center"
      >
        {label}
      </motion.p>
    </motion.div>
  );
}
