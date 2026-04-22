const { Temporal } = require('@js-temporal/polyfill');

module.exports = {
    /**
    * Converts a time string to a standardized format.
    * @param {string | Date | Temporal.Instant} timeString - The time string to convert.
    * @returns {string} The converted time string.
    */
    convertToISOTimeString: (timeString) => {
        let convertedTimeString;

        if (timeString instanceof Temporal.Instant) {
            convertedTimeString = timeString.toString();
        } else if (timeString instanceof Date) {
            if (isNaN(timeString.getTime())) {
                throw new RangeError("Cannot convert an invalid Date object.");
            }
            convertedTimeString = timeString.toISOString();
        } else if (typeof timeString === 'string') {
            convertedTimeString = Temporal.Instant.from(timeString).toString();
        }

        return convertedTimeString;
    },
};
