

	function TestProxyInterfaceProxy($http) {}

    TestProxyInterfaceProxy.prototype.baz = function(ago) {
        dispatchCall("POST", "baz", [ago]);
    };
