function openFile (success, error, opts) {
  window.alert('test')
}

module.exports = {
  openFile: openFile
}

require('cordova/exec/proxy').add('LocalHelper', module.exports)
