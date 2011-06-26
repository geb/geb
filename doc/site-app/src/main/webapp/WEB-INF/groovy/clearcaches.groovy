log.warning "Clearing caches"
memcache.clearAll()
out << "caches cleared"
out.flush()
out.close()