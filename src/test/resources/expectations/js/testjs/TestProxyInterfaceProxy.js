

function TestProxyInterfaceProxy($http) {
    this.$http = $http;
}

TestProxyInterfaceProxy.prototype.baz = function(ago, value) {
    return this.dispatchCall("POST", "baz", {
        ago: ago,
        value: value
    });
};
