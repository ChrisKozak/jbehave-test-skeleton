describe("human readable duration", function(){

    TestController = function(){};
    TestController.prototype = MyController.prototype;
    var controller = new TestController();
    
    it("should transform the duration in milliseconds to a duration in minutes and seconds", function(){
        expect(controller.humanReadableDuration(71000)).toEqual("1m 11s");
    });

    it("should not say zero minutes if the duration is less than 60 seconds", function(){
        expect(controller.humanReadableDuration(10000)).toEqual("10s");
    });

    it("should only show whole seconds", function(){
        expect(controller.humanReadableDuration(1100)).toEqual("1s");
        expect(controller.humanReadableDuration(1700)).toEqual("2s");
    });
});