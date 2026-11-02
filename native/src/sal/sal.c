/* PQ navigation-only SAL preload: starts the iAP2 Route Guidance bridge. */
#include "mcn_hook.h"
#include "mcn_route.h"

void mcn_route_start_worker(void);

const char mcn_component[] = "sal";

MCN_EXPORT void mcn_init(void)
{
    mcn_log("PQ CarPlay route hook loaded (no video path)");
    mcn_guard_init();
    mcn_route_start_worker();
}
