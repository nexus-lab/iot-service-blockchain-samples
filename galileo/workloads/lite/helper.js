const uuid = require('uuid');

module.exports.uuid = function _(mspId, deviceId, txIndex) {
  return uuid.v5(`${mspId}_${deviceId}_${txIndex}`, uuid.NIL);
};
