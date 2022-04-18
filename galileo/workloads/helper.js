const uuid = require('uuid');

module.exports.uuid = function _(mspId, identityName) {
  return uuid.v5(`${mspId}_${identityName}`, uuid.NIL);
};
