interface TechData {
  cms: string
  server: string
  analytics: string[]
  frameworks: string[]
}

interface TechnologyStackProps {
  data: TechData
}

export function TechnologyStack({ data }: TechnologyStackProps) {
  return (
    <div className="bg-white dark:bg-gray-800 rounded-lg p-6 shadow-sm border border-gray-200 dark:border-gray-700">
      <h3 className="text-lg font-semibold text-gray-900 dark:text-white mb-4">
        Technology Stack
      </h3>

      <div className="space-y-4">
        <div>
          <h4 className="text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
            Content Management System
          </h4>
          <span className="inline-block px-3 py-1 bg-blue-100 dark:bg-blue-900 text-blue-800 dark:text-blue-200 text-sm rounded-full">
            {data.cms}
          </span>
        </div>

        <div>
          <h4 className="text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
            Server
          </h4>
          <span className="inline-block px-3 py-1 bg-green-100 dark:bg-green-900 text-green-800 dark:text-green-200 text-sm rounded-full">
            {data.server}
          </span>
        </div>

        <div>
          <h4 className="text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
            Analytics Tools
          </h4>
          <div className="flex flex-wrap gap-2">
            {data.analytics.map((tool, index) => (
              <span
                key={index}
                className="px-3 py-1 bg-purple-100 dark:bg-purple-900 text-purple-800 dark:text-purple-200 text-sm rounded-full"
              >
                {tool}
              </span>
            ))}
          </div>
        </div>

        <div>
          <h4 className="text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
            Frameworks
          </h4>
          <div className="flex flex-wrap gap-2">
            {data.frameworks.map((framework, index) => (
              <span
                key={index}
                className="px-3 py-1 bg-orange-100 dark:bg-orange-900 text-orange-800 dark:text-orange-200 text-sm rounded-full"
              >
                {framework}
              </span>
            ))}
          </div>
        </div>
      </div>
    </div>
  )
}
