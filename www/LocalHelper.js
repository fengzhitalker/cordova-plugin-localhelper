var exec = require('cordova/exec')

var localhelperExport = {}
localhelperExport.openFile = function (success, error) {
  exec(success, error, 'LocalHelper', 'openFile', [])
}

module.exports = localhelperExport
