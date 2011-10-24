describe("super merge", function() {

  it("should merge objects with different property names", function() {
    var result = FTA.superMerge(true, {a:{b:1}},{a:{c:1}});
    expect(result).toEqual({a:{b:1,c:1}});
  });

  it("should merge list properties", function() {
    var result = FTA.superMerge(true, {a:[1]},{a:[2]});
    expect(result).toEqual({a:[1,2]});
  });
 
});

describe("truncate element text", function() {

  it("should set the text of the element to be the input text", function() {
    var element = $("<div>");
    FTA.truncateElementText(element, "text");
    expect(element.text()).toEqual("text");
  });

  it("should truncate after X characters if text is too long", function(){
    var element = $("<div>");
    var text = "";
    for (var i = 0; i < 10; i++) text += "-";
    FTA.truncateElementText(element, text, 5);
    expect(element.text()).toEqual("--...");
    expect(element.attr('title')).toEqual(text);
  });

});

describe("eliminate duplicates", function() {
  it("should remove duplicate elements in array", function(){
    expect(FTA.eliminateDuplicates(['1','2','3','2'])).toEqual(['1','2','3']);
  });
});
