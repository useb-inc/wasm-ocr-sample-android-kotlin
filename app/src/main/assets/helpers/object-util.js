function _defineProperty(obj, key, value) { key = _toPropertyKey(key); if (key in obj) { Object.defineProperty(obj, key, { value: value, enumerable: true, configurable: true, writable: true }); } else { obj[key] = value; } return obj; }
function _toPropertyKey(arg) { var key = _toPrimitive(arg, "string"); return typeof key === "symbol" ? key : String(key); }
function _toPrimitive(input, hint) { if (typeof input !== "object" || input === null) return input; var prim = input[Symbol.toPrimitive]; if (prim !== undefined) { var res = prim.call(input, hint || "default"); if (typeof res !== "object") return res; throw new TypeError("@@toPrimitive must return a primitive value."); } return (hint === "string" ? String : Number)(input); }
/* eslint-disable */

/* global-module */
class ObjectUtil {
  constructor() {
    _defineProperty(this, "objectDeepMerge", (target, source) => {
      for (var key of Object.keys(source)) {
        if (source[key] instanceof Object) Object.assign(source[key], this.objectDeepMerge(target[key], source[key]) // 재귀
        );
      }

      Object.assign(target || {}, source);
      return target;
    });
  }
  stringToJson(str) {
    var obj = {};
    var keyValuePairs = str.match(/\w+:(?:\([^)]*\)|[^;]*)/g);
    if (keyValuePairs) {
      for (var i = 0; i < keyValuePairs.length; i++) {
        var pair = keyValuePairs[i].split(':');
        var key = pair[0].trim();
        var value = pair.slice(1).join(':').trim();
        if (value.startsWith('(') && value.endsWith(')')) {
          var subStr = value.substring(1, value.length - 1); // 서브 문자열 추출
          var subObj = this.stringToJson(subStr); // 재귀적으로 서브 오브젝트 변환
          obj[key] = subObj;
        } else {
          obj[key] = value;
        }
      }
    }
    return obj;
  }
  getObjectValueWithDot(obj, key) {
    if (obj) {
      if (key.split('.').length === 1) {
        return obj[key];
      }
      var tmpKey = key.split('.')[0];
      var tmpKey2 = key.slice(tmpKey.length + 1, key.length);
      return this.getObjectValueWithDot(obj[tmpKey], tmpKey2); // 재귀
    }
  }

  makeObjectWithDot(obj, key, value) {
    var mainKey = arguments.length > 3 && arguments[3] !== undefined ? arguments[3] : null;
    var tmpKey = key.split('.')[0];
    if (key.split('.').length === 1) {
      obj[key] = value;
      return mainKey === null ? tmpKey : mainKey;
    }
    var tmpKey2 = key.slice(tmpKey.length + 1, key.length);

    // make object for key
    obj[tmpKey] = {};
    return this.makeObjectWithDot(obj[tmpKey], tmpKey2, value, tmpKey); // 재귀
  }
}

export default new ObjectUtil();
//# sourceMappingURL=data:application/json;charset=utf8;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoiaGVscGVycy9vYmplY3QtdXRpbC5qcyIsIm5hbWVzIjpbIk9iamVjdFV0aWwiLCJjb25zdHJ1Y3RvciIsIl9kZWZpbmVQcm9wZXJ0eSIsInRhcmdldCIsInNvdXJjZSIsImtleSIsIk9iamVjdCIsImtleXMiLCJhc3NpZ24iLCJvYmplY3REZWVwTWVyZ2UiLCJzdHJpbmdUb0pzb24iLCJzdHIiLCJvYmoiLCJrZXlWYWx1ZVBhaXJzIiwibWF0Y2giLCJpIiwibGVuZ3RoIiwicGFpciIsInNwbGl0IiwidHJpbSIsInZhbHVlIiwic2xpY2UiLCJqb2luIiwic3RhcnRzV2l0aCIsImVuZHNXaXRoIiwic3ViU3RyIiwic3Vic3RyaW5nIiwic3ViT2JqIiwiZ2V0T2JqZWN0VmFsdWVXaXRoRG90IiwidG1wS2V5IiwidG1wS2V5MiIsIm1ha2VPYmplY3RXaXRoRG90IiwibWFpbktleSIsImFyZ3VtZW50cyIsInVuZGVmaW5lZCJdLCJzb3VyY2VzIjpbImhlbHBlcnMvb2JqZWN0LXV0aWwuanMiXSwic291cmNlc0NvbnRlbnQiOlsiLyogZXNsaW50LWRpc2FibGUgKi9cblxuLyogZ2xvYmFsLW1vZHVsZSAqL1xuY2xhc3MgT2JqZWN0VXRpbCB7XG4gIHN0cmluZ1RvSnNvbihzdHIpIHtcbiAgICBsZXQgb2JqID0ge307XG5cbiAgICBsZXQga2V5VmFsdWVQYWlycyA9IHN0ci5tYXRjaCgvXFx3KzooPzpcXChbXildKlxcKXxbXjtdKikvZyk7XG4gICAgaWYgKGtleVZhbHVlUGFpcnMpIHtcbiAgICAgIGZvciAobGV0IGkgPSAwOyBpIDwga2V5VmFsdWVQYWlycy5sZW5ndGg7IGkrKykge1xuICAgICAgICBsZXQgcGFpciA9IGtleVZhbHVlUGFpcnNbaV0uc3BsaXQoJzonKTtcbiAgICAgICAgbGV0IGtleSA9IHBhaXJbMF0udHJpbSgpO1xuICAgICAgICBsZXQgdmFsdWUgPSBwYWlyLnNsaWNlKDEpLmpvaW4oJzonKS50cmltKCk7XG5cbiAgICAgICAgaWYgKHZhbHVlLnN0YXJ0c1dpdGgoJygnKSAmJiB2YWx1ZS5lbmRzV2l0aCgnKScpKSB7XG4gICAgICAgICAgbGV0IHN1YlN0ciA9IHZhbHVlLnN1YnN0cmluZygxLCB2YWx1ZS5sZW5ndGggLSAxKTsgLy8g7ISc67iMIOusuOyekOyXtCDstpTstpxcbiAgICAgICAgICBsZXQgc3ViT2JqID0gdGhpcy5zdHJpbmdUb0pzb24oc3ViU3RyKTsgLy8g7J6s6reA7KCB7Jy866GcIOyEnOu4jCDsmKTruIzsoJ3tirgg67OA7ZmYXG4gICAgICAgICAgb2JqW2tleV0gPSBzdWJPYmo7XG4gICAgICAgIH0gZWxzZSB7XG4gICAgICAgICAgb2JqW2tleV0gPSB2YWx1ZTtcbiAgICAgICAgfVxuICAgICAgfVxuICAgIH1cblxuICAgIHJldHVybiBvYmo7XG4gIH1cblxuICBnZXRPYmplY3RWYWx1ZVdpdGhEb3Qob2JqLCBrZXkpIHtcbiAgICBpZiAob2JqKSB7XG4gICAgICBpZiAoa2V5LnNwbGl0KCcuJykubGVuZ3RoID09PSAxKSB7XG4gICAgICAgIHJldHVybiBvYmpba2V5XTtcbiAgICAgIH1cblxuICAgICAgY29uc3QgdG1wS2V5ID0ga2V5LnNwbGl0KCcuJylbMF07XG4gICAgICBjb25zdCB0bXBLZXkyID0ga2V5LnNsaWNlKHRtcEtleS5sZW5ndGggKyAxLCBrZXkubGVuZ3RoKTtcbiAgICAgIHJldHVybiB0aGlzLmdldE9iamVjdFZhbHVlV2l0aERvdChvYmpbdG1wS2V5XSwgdG1wS2V5Mik7IC8vIOyerOq3gFxuICAgIH1cbiAgfVxuXG4gIG1ha2VPYmplY3RXaXRoRG90KG9iaiwga2V5LCB2YWx1ZSwgbWFpbktleSA9IG51bGwpIHtcbiAgICBjb25zdCB0bXBLZXkgPSBrZXkuc3BsaXQoJy4nKVswXTtcblxuICAgIGlmIChrZXkuc3BsaXQoJy4nKS5sZW5ndGggPT09IDEpIHtcbiAgICAgIG9ialtrZXldID0gdmFsdWU7XG4gICAgICByZXR1cm4gbWFpbktleSA9PT0gbnVsbCA/IHRtcEtleSA6IG1haW5LZXk7XG4gICAgfVxuXG4gICAgY29uc3QgdG1wS2V5MiA9IGtleS5zbGljZSh0bXBLZXkubGVuZ3RoICsgMSwga2V5Lmxlbmd0aCk7XG5cbiAgICAvLyBtYWtlIG9iamVjdCBmb3Iga2V5XG4gICAgb2JqW3RtcEtleV0gPSB7fTtcblxuICAgIHJldHVybiB0aGlzLm1ha2VPYmplY3RXaXRoRG90KG9ialt0bXBLZXldLCB0bXBLZXkyLCB2YWx1ZSwgdG1wS2V5KTsgLy8g7J6s6reAXG4gIH1cblxuICBvYmplY3REZWVwTWVyZ2UgPSAodGFyZ2V0LCBzb3VyY2UpID0+IHtcbiAgICBmb3IgKGxldCBrZXkgb2YgT2JqZWN0LmtleXMoc291cmNlKSkge1xuICAgICAgaWYgKHNvdXJjZVtrZXldIGluc3RhbmNlb2YgT2JqZWN0KVxuICAgICAgICBPYmplY3QuYXNzaWduKFxuICAgICAgICAgIHNvdXJjZVtrZXldLFxuICAgICAgICAgIHRoaXMub2JqZWN0RGVlcE1lcmdlKHRhcmdldFtrZXldLCBzb3VyY2Vba2V5XSkgLy8g7J6s6reAXG4gICAgICAgICk7XG4gICAgfVxuXG4gICAgT2JqZWN0LmFzc2lnbih0YXJnZXQgfHwge30sIHNvdXJjZSk7XG4gICAgcmV0dXJuIHRhcmdldDtcbiAgfTtcbn1cblxuZXhwb3J0IGRlZmF1bHQgbmV3IE9iamVjdFV0aWwoKTtcbiJdLCJtYXBwaW5ncyI6Ijs7O0FBQUE7O0FBRUE7QUFDQSxNQUFNQSxVQUFVLENBQUM7RUFBQUMsWUFBQTtJQUFBQyxlQUFBLDBCQW9ERyxDQUFDQyxNQUFNLEVBQUVDLE1BQU0sS0FBSztNQUNwQyxLQUFLLElBQUlDLEdBQUcsSUFBSUMsTUFBTSxDQUFDQyxJQUFJLENBQUNILE1BQU0sQ0FBQyxFQUFFO1FBQ25DLElBQUlBLE1BQU0sQ0FBQ0MsR0FBRyxDQUFDLFlBQVlDLE1BQU0sRUFDL0JBLE1BQU0sQ0FBQ0UsTUFBTSxDQUNYSixNQUFNLENBQUNDLEdBQUcsQ0FBQyxFQUNYLElBQUksQ0FBQ0ksZUFBZSxDQUFDTixNQUFNLENBQUNFLEdBQUcsQ0FBQyxF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
