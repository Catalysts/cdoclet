

function TestProxyInterfaceProxy($http) {
    this.$http = $http;
}

TestProxyInterfaceProxy.prototype.baz = function(ago) {
    return this.dispatchCall("POST", "baz", [ago]);
};
