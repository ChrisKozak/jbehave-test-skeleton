FTA = {};

FTA.truncateElementText = function(element, input_text, sizeLimit){
   sizeLimit = sizeLimit || 50;
   var jquery_element = $(element);
   if (input_text === undefined || input_text.length < sizeLimit) {
      jquery_element.text(input_text);
   } else {
      jquery_element.attr("title", input_text);
      jquery_element.text(input_text.substring(0, sizeLimit - 3) + '...');
   }
   return jquery_element.text();
};

// this is copied from jQuery#extend and modified so that lists don't get overwritten but merged
FTA.superMerge = function() {
    var options, name, src, copy, copyIsArray, clone,
        target = arguments[0] || {},
        i = 1,
        length = arguments.length,
        deep = false;

    // Handle a deep copy situation
    if ( typeof target === "boolean" ) {
        deep = target;
        target = arguments[1] || {};
        // skip the boolean and the target
        i = 2;
    }

    // Handle case when target is a string or something (possible in deep copy)
    if ( typeof target !== "object" && !jQuery.isFunction(target) ) {
        target = {};
    }

    // extend jQuery itself if only one argument is passed
    if ( length === i ) {
        target = this;
        --i;
    }

    for ( ; i < length; i++ ) {
        // Only deal with non-null/undefined values
        if ( (options = arguments[ i ]) != null ) {
            // Extend the base object
            for ( name in options ) {
                src = target[ name ];
                copy = options[ name ];

                // Prevent never-ending loop
                if ( target === copy ) {
                    continue;
                }

                // Recurse if we're merging plain objects or arrays
                if ( deep && copy && ( jQuery.isPlainObject(copy) || (copyIsArray = jQuery.isArray(copy)) ) ) {
                    if ( copyIsArray ) {
                        copyIsArray = false;
                        clone = src && jQuery.isArray(src) ? src : [];
                        target[ name ] = jQuery.merge(clone, copy);

                    } else {
                        clone = src && jQuery.isPlainObject(src) ? src : {};
                      // Never move original objects, clone them
                      target[ name ] = FTA.superMerge( deep, clone, copy );

                    }

                // Don't bring in undefined values
                } else if ( copy !== undefined ) {
                    target[ name ] = copy;
                }
            }
        }
    }

    // Return the modified object
    return target;
};

FTA.eliminateDuplicates = function(arr) {
  var i;
  var len = arr.length;
  var out = [];
  var obj = {};

  for (i = 0; i < len; i++) {
    obj[arr[i]]=0;
  }

  for (i in obj) {
    out.push(i);
  }

  return out;
};
