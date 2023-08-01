import hudson.model.Computer

// Function to convert bytes to a human-readable format (MB or GB).
def humanReadableSize(sizeInBytes) {
    def kb = sizeInBytes / 1024
    if (kb < 1024) {
        return "${kb} KB"
    } else {
        def mb = kb / 1024
        if (mb < 1024) {
            return "${mb} MB"
        } else {
            def gb = mb / 1024
            return "${gb} GB"
        }
    }
}

// Function to get the memory usage from the node's monitoring data.
def getMemoryUsage(node) {
    def monitorData = node.monitorData
    def memoryUsageData = monitorData.get('hudson.node_monitors.SwapSpaceMonitor')
    if (memoryUsageData) {
        return memoryUsageData.availableSwapSpace
    } else {
        return null
    }
}

// Define the time range for the last month (30 days).
def timeRange = 30 * 24 * 60 * 60 * 1000 // 30 days in milliseconds

// Get the current time in milliseconds.
def currentTimeMillis = System.currentTimeMillis()

// Calculate the start time for the last month.
def startTimeMillis = currentTimeMillis - timeRange

// Iterate through all the Jenkins build agents (nodes).
for (node in Jenkins.instance.nodes) {
    if (!node.toComputer().offline) {
        def nodeName = node.name
        def computer = node.toComputer()

        if (computer && computer.isOnline()) {
            def memoryUsage = getMemoryUsage(computer)
            if (memoryUsage != null) {
                println "Build agent ${nodeName} memory usage in the last month: ${humanReadableSize(memoryUsage)}"
            } else {
                println "Build agent ${nodeName} memory usage data not available for the last month."
            }
        }
    }
}
