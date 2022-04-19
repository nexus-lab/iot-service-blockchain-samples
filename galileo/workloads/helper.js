const uuid = require('uuid');

module.exports.uuid = function _(mspId, deviceId) {
  return uuid.v5(`${mspId}_${deviceId}`, uuid.NIL);
};
