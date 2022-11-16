// polyfill
if (!String.fromCodePoint) {
  var stringFromCharCode = String.fromCharCode;
  var floor = Math.floor;
  Object.defineProperty(String, 'fromCodePoint', {
    configurable: true,
    writable: true,
    value: function () {
      var MAX_SIZE = 0x4000;
      var codeUnits = [];
      var highSurrogate;
      var lowSurrogate;
      var index = -1;
      var length = arguments.length;
      if (!length) {
        return '';
      }
      var result = '';
      while (++index < length) {
        var codePoint = Number(arguments[index]);
        if (
          !isFinite(codePoint) ||
          codePoint < 0 ||
          codePoint > 0x10ffff ||
          floor(codePoint) != codePoint
        ) {
          throw RangeError('Invalid code point: ' + codePoint);
        }
        if (codePoint <= 0xffff) {
          // BMP code point
          codeUnits.push(codePoint);
        } else {
          codePoint -= 0x10000;
          highSurrogate = (codePoint >> 10) + 0xd800;
          lowSurrogate = (codePoint % 0x400) + 0xdc00;
          codeUnits.push(highSurrogate, lowSurrogate);
        }
        if (index + 1 == length || codeUnits.length > MAX_SIZE) {
          result += stringFromCharCode.apply(null, codeUnits);
          codeUnits.length = 0;
        }
      }
      return result;
    },
  });
}

if (!String.prototype.codePointAt) {
  Object.defineProperty(String.prototype, 'codePointAt', {
    configurable: true,
    writable: true,
    value: function (position) {
      if (this == null) {
        throw TypeError();
      }
      var string = String(this);
      var size = string.length;
      var index = position ? Number(position) : 0;
      if (index != index) {
        index = 0;
      }
      if (index < 0 || index >= size) {
        return undefined;
      }
      var first = string.charCodeAt(index);
      var second;
      if (first >= 0xd800 && first <= 0xdbff && size > index + 1) {
        second = string.charCodeAt(index + 1);
        if (second >= 0xdc00 && second <= 0xdfff) {
          return (first - 0xd800) * 0x400 + second - 0xdc00 + 0x10000;
        }
      }
      return first;
    },
  });
}

if (!Array.isArray) {
  Array.isArray = function (arg) {
    return Object.prototype.toString.call(arg) === '[object Array]';
  };
}

if (!Array.from) {
  Array.from = (function () {
    var toStr = Object.prototype.toString;
    var isCallable = function (fn) {
      return typeof fn === 'function' || toStr.call(fn) === '[object Function]';
    };
    var toInteger = function (value) {
      var number = Number(value);
      if (isNaN(number)) {
        return 0;
      }
      if (number === 0 || !isFinite(number)) {
        return number;
      }
      return (number > 0 ? 1 : -1) * Math.floor(Math.abs(number));
    };
    var maxSafeInteger = Math.pow(2, 53) - 1;
    var toLength = function (value) {
      var len = toInteger(value);
      return Math.min(Math.max(len, 0), maxSafeInteger);
    };

    // The length property of the from method is 1.
    return function from(arrayLike /*, mapFn, thisArg */) {
      // 1. Let C be the this value.
      var C = this;

      // 2. Let items be ToObject(arrayLike).
      var items = Object(arrayLike);

      // 3. ReturnIfAbrupt(items).
      if (arrayLike == null) {
        throw new TypeError(
          'Array.from requires an array-like object - not null or undefined'
        );
      }

      // 4. If mapfn is undefined, then let mapping be false.
      var mapFn = arguments.length > 1 ? arguments[1] : void undefined;
      var T;
      if (typeof mapFn !== 'undefined') {
        // 5. else
        // 5. a If IsCallable(mapfn) is false, throw a TypeError exception.
        if (!isCallable(mapFn)) {
          throw new TypeError(
            'Array.from: when provided, the second argument must be a function'
          );
        }

        // 5. b. If thisArg was supplied, let T be thisArg; else let T be undefined.
        if (arguments.length > 2) {
          T = arguments[2];
        }
      }

      // 10. Let lenValue be Get(items, "length").
      // 11. Let len be ToLength(lenValue).
      var len = toLength(items.length);

      // 13. If IsConstructor(C) is true, then
      // 13. a. Let A be the result of calling the [[Construct]] internal method
      // of C with an argument list containing the single item len.
      // 14. a. Else, Let A be ArrayCreate(len).
      var A = isCallable(C) ? Object(new C(len)) : new Array(len);

      // 16. Let k be 0.
      var k = 0;
      // 17. Repeat, while k < len… (also steps a - h)
      var kValue;
      while (k < len) {
        kValue = items[k];
        if (mapFn) {
          A[k] =
            typeof T === 'undefined'
              ? mapFn(kValue, k)
              : mapFn.call(T, kValue, k);
        } else {
          A[k] = kValue;
        }
        k += 1;
      }
      // 18. Let putStatus be Put(A, "length", len, true).
      A.length = len;
      // 20. Return A.
      return A;
    };
  })();
}

if (!Array.prototype.map) {
  Object.defineProperty(Array.prototype, 'map', {
    configurable: true,
    writable: true,
    value: function (callback /*, thisArg*/) {
      var T, A, k;

      if (this == null) {
        throw new TypeError('this is null or not defined');
      }

      // 1. Let O be the result of calling ToObject passing the |this|
      //    value as the argument.
      var O = Object(this);

      // 2. Let lenValue be the result of calling the Get internal
      //    method of O with the argument "length".
      // 3. Let len be ToUint32(lenValue).
      var len = O.length >>> 0;

      // 4. If IsCallable(callback) is false, throw a TypeError exception.
      // See: http://es5.github.com/#x9.11
      if (typeof callback !== 'function') {
        throw new TypeError(callback + ' is not a function');
      }

      // 5. If thisArg was supplied, let T be thisArg; else let T be undefined.
      if (arguments.length > 1) {
        T = arguments[1];
      }

      // 6. Let A be a new array created as if by the expression new Array(len)
      //    where Array is the standard built-in constructor with that name and
      //    len is the value of len.
      A = new Array(len);

      // 7. Let k be 0
      k = 0;

      // 8. Repeat, while k < len
      while (k < len) {
        var kValue, mappedValue;

        // a. Let Pk be ToString(k).
        //   This is implicit for LHS operands of the in operator
        // b. Let kPresent be the result of calling the HasProperty internal
        //    method of O with argument Pk.
        //   This step can be combined with c
        // c. If kPresent is true, then
        if (k in O) {
          // i. Let kValue be the result of calling the Get internal
          //    method of O with argument Pk.
          kValue = O[k];

          // ii. Let mappedValue be the result of calling the Call internal
          //     method of callback with T as the this value and argument
          //     list containing kValue, k, and O.
          mappedValue = callback.call(T, kValue, k, O);

          // iii. Call the DefineOwnProperty internal method of A with arguments
          // Pk, Property Descriptor
          // { Value: mappedValue,
          //   Writable: true,
          //   Enumerable: true,
          //   Configurable: true },
          // and false.

          // In browsers that support Object.defineProperty, use the following:
          // Object.defineProperty(A, k, {
          //   value: mappedValue,
          //   writable: true,
          //   enumerable: true,
          //   configurable: true
          // });

          // For best browser support, use the following:
          A[k] = mappedValue;
        }
        // d. Increase k by 1.
        k++;
      }

      // 9. return A
      return A;
    },
  });
}


sm4=function(r){function n(o){if(t[o])return t[o].exports;var e=t[o]={i:o,l:!1,exports:{}};return r[o].call(e.exports,e,e.exports,n),e.l=!0,e.exports}var t={};return n.m=r,n.c=t,n.d=function(r,t,o){n.o(r,t)||Object.defineProperty(r,t,{configurable:!1,enumerable:!0,get:o})},n.n=function(r){var t=r&&r.__esModule?function(){return r.default}:function(){return r};return n.d(t,"a",t),t},n.o=function(r,n){return Object.prototype.hasOwnProperty.call(r,n)},n.p="",n(n.s=7)}({7:function(r,n,t){"use strict";function o(r){if(Array.isArray(r)){for(var n=0,t=Array(r.length);n<r.length;n++)t[n]=r[n];return t}return Array.from(r)}function e(r){for(var n=[],t=0,o=r.length;t<o;t+=2)n.push(parseInt(r.substr(t,2),16));return n}function i(r){return r.map(function(r){return r=r.toString(16),1===r.length?"0"+r:r}).join("")}function u(r){for(var n=[],t=0,o=r.length;t<o;t++){var e=r.codePointAt(t);if(e<=127)n.push(e);else if(e<=2047)n.push(192|e>>>6),n.push(128|63&e);else if(e<=55295||e>=57344&&e<=65535)n.push(224|e>>>12),n.push(128|e>>>6&63),n.push(128|63&e);else{if(!(e>=65536&&e<=1114111))throw n.push(e),new Error("input is not supported");t++,n.push(240|e>>>18&28),n.push(128|e>>>12&63),n.push(128|e>>>6&63),n.push(128|63&e)}}return n}function f(r){for(var n=[],t=0,o=r.length;t<o;t++)r[t]>=240&&r[t]<=247?(n.push(fromCodePoint(((7&r[t])<<18)+((63&r[t+1])<<12)+((63&r[t+2])<<6)+(63&r[t+3]))),t+=3):r[t]>=224&&r[t]<=239?(n.push(fromCodePoint(((15&r[t])<<12)+((63&r[t+1])<<6)+(63&r[t+2]))),t+=2):r[t]>=192&&r[t]<=223?(n.push(fromCodePoint(((31&r[t])<<6)+(63&r[t+1]))),t++):n.push(fromCodePoint(r[t]));return n.join("")}function s(r,n){return r<<n|r>>>32-n}function a(r){return(255&w[r>>>24&255])<<24|(255&w[r>>>16&255])<<16|(255&w[r>>>8&255])<<8|255&w[255&r]}function c(r){return r^s(r,2)^s(r,10)^s(r,18)^s(r,24)}function p(r){return r^s(r,13)^s(r,23)}function h(r,n,t){for(var o=new Array(4),e=new Array(4),i=0;i<4;i++)e[0]=255&r[4*i],e[1]=255&r[4*i+1],e[2]=255&r[4*i+2],e[3]=255&r[4*i+3],o[i]=e[0]<<24|e[1]<<16|e[2]<<8|e[3];for(var u,f=0;f<32;f+=4)u=o[1]^o[2]^o[3]^t[f+0],o[0]^=c(a(u)),u=o[2]^o[3]^o[0]^t[f+1],o[1]^=c(a(u)),u=o[3]^o[0]^o[1]^t[f+2],o[2]^=c(a(u)),u=o[0]^o[1]^o[2]^t[f+3],o[3]^=c(a(u));for(var s=0;s<16;s+=4)n[s]=o[3-s/4]>>>24&255,n[s+1]=o[3-s/4]>>>16&255,n[s+2]=o[3-s/4]>>>8&255,n[s+3]=255&o[3-s/4]}function v(r,n,t){for(var o=new Array(4),e=new Array(4),i=0;i<4;i++)e[0]=255&r[0+4*i],e[1]=255&r[1+4*i],e[2]=255&r[2+4*i],e[3]=255&r[3+4*i],o[i]=e[0]<<24|e[1]<<16|e[2]<<8|e[3];o[0]^=2746333894,o[1]^=1453994832,o[2]^=1736282519,o[3]^=2993693404;for(var u,f=0;f<32;f+=4)u=o[1]^o[2]^o[3]^A[f+0],n[f+0]=o[0]^=p(a(u)),u=o[2]^o[3]^o[0]^A[f+1],n[f+1]=o[1]^=p(a(u)),u=o[3]^o[0]^o[1]^A[f+2],n[f+2]=o[2]^=p(a(u)),u=o[0]^o[1]^o[2]^A[f+3],n[f+3]=o[3]^=p(a(u));if(t===g)for(var s,c=0;c<16;c++)s=n[c],n[c]=n[31-c],n[31-c]=s}function l(r,n,t){var s=arguments.length>3&&void 0!==arguments[3]?arguments[3]:{},a=s.padding,c=void 0===a?"pkcs#5":a,p=s.mode,l=s.iv,w=void 0===l?[]:l,A=s.output,m=void 0===A?"string":A;if("cbc"===p&&("string"==typeof w&&(w=e(w)),16!==w.length))throw new Error("iv is invalid");if("string"==typeof n&&(n=e(n)),16!==n.length)throw new Error("key is invalid");if(r="string"==typeof r?t!==g?u(r):e(r):[].concat(o(r)),"pkcs#5"===c&&t!==g)for(var b=y-r.length%y,P=0;P<b;P++)r.push(b);var x=new Array(d);v(n,x,t);for(var S=[],j=w,k=r.length,C=0;k>=y;){var E=r.slice(C,C+16),O=new Array(16);if("cbc"===p)for(var _=0;_<y;_++)t!==g&&(E[_]^=j[_]);h(E,O,x);for(var I=0;I<y;I++)"cbc"===p&&t===g&&(O[I]^=j[I]),S[C+I]=O[I];"cbc"===p&&(j=t!==g?O:E),k-=y,C+=y}if("pkcs#5"===c&&t===g){var M=S[S.length-1];S.splice(S.length-M,M)}return"array"!==m?t!==g?i(S):f(S):S}var g=0,d=32,y=16,w=[214,144,233,254,204,225,61,183,22,182,20,194,40,251,44,5,43,103,154,118,42,190,4,195,170,68,19,38,73,134,6,153,156,66,80,244,145,239,152,122,51,84,11,67,237,207,172,98,228,179,28,169,201,8,232,149,128,223,148,250,117,143,63,166,71,7,167,252,243,115,23,186,131,89,60,25,230,133,79,168,104,107,129,178,113,100,218,139,248,235,15,75,112,86,157,53,30,36,14,94,99,88,209,162,37,34,124,59,1,33,120,135,212,0,70,87,159,211,39,82,76,54,2,231,160,196,200,158,234,191,138,210,64,199,56,181,163,247,242,206,249,97,21,161,224,174,93,164,155,52,26,85,173,147,50,48,245,140,177,227,29,246,226,46,130,102,202,96,192,41,35,171,13,83,78,111,213,219,55,69,222,253,142,47,3,255,106,114,109,108,91,81,141,27,175,146,187,221,188,127,17,217,92,65,31,16,90,216,10,193,49,136,165,205,123,189,45,116,208,18,184,229,180,176,137,105,151,74,12,150,119,126,101,185,241,9,197,110,198,132,24,240,125,236,58,220,77,32,121,238,95,62,215,203,57,72],A=[462357,472066609,943670861,1415275113,1886879365,2358483617,2830087869,3301692121,3773296373,4228057617,404694573,876298825,1347903077,1819507329,2291111581,2762715833,3234320085,3705924337,4177462797,337322537,808926789,1280531041,1752135293,2223739545,2695343797,3166948049,3638552301,4110090761,269950501,741554753,1213159005,1684763257];r.exports={encrypt:function(r,n,t){return l(r,n,1,t)},decrypt:function(r,n,t){return l(r,n,0,t)}}}});

fromCodePoint = function () {var stringFromCharCode = String.fromCharCode;var floor = Math.floor;var MAX_SIZE = 0x4000;var codeUnits = [];var highSurrogate;var lowSurrogate;var index = -1;var length = arguments.length;if (!length) {return '';}var result = '';while (++index < length) {var codePoint = Number(arguments[index]);if (!isFinite(codePoint) || codePoint < 0 || codePoint > 0x10FFFF || floor(codePoint) != codePoint) {throw RangeError('Invalid code point: ' + codePoint);}if (codePoint <= 0xFFFF) {codeUnits.push(codePoint);} else {codePoint -= 0x10000;highSurrogate = (codePoint >> 10) + 0xD800;lowSurrogate = (codePoint % 0x400) + 0xDC00;codeUnits.push(highSurrogate, lowSurrogate);}if (index + 1 == length || codeUnits.length > MAX_SIZE) {result += stringFromCharCode.apply(null, codeUnits);codeUnits.length = 0;}}return result;}

/**
 * 加密
 */
encrypt = function (msg, key, options) {
    return sm4.encrypt(msg, key, 1, options)
}

/**
 * 解密
 */
decrypt = function (encryptData, key, options) {
    return sm4.decrypt(encryptData, key, 0, options)
}
