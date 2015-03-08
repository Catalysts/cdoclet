

function TestProxyInterfaceProxy($http) {
    this.$http = $http;
}

TestProxyInterfaceProxy.prototype.baz = function(ago) {
    this.dispatchCall("POST", "baz", [ago]);
};
