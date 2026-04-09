const { S3Client, PutObjectCommand, GetObjectCommand, DeleteObjectCommand } = require("@aws-sdk/client-s3");
const { AWS_HOST, AWS_REGION, AWS_ACCESS_KEY_ID, AWS_SECRET_ACCESS_KEY } = require('../configs/env.config');

/**
 * Service for interacting with AWS S3, providing methods to upload, retrieve, and delete objects in S3 buckets.
 */
class S3Service {
    static BUCKETS = ['clothes-images'];

    /**
    * @param {any} config - Optional config override for testing.
    */
    constructor(config = {}) {
        this.s3Client = new S3Client({
            endpoint: config.endpoint || AWS_HOST,
            region: config.region || AWS_REGION,
            credentials: {
                accessKeyId: config.accessKeyId || AWS_ACCESS_KEY_ID,
                secretAccessKey: config.secretAccessKey || AWS_SECRET_ACCESS_KEY,
            },
        });
    }

    /**
    * Uploads an object to S3.
    * @param {string} bucket - The name of the S3 bucket.
    * @param {Buffer} file - The file data to upload.
    * @param {string} fileName - The name to assign to the uploaded file in S3.
    * @returns {Promise<import("@aws-sdk/client-s3").PutObjectCommandOutput>} The response from S3 after attempting to upload the object.
    */
    async putObject(bucket, file, fileName, contentType) {
        const params = {
            Bucket: bucket,
            Key: fileName,
            Body: file,
            ContentType: contentType || "application/octet-stream",
        };

        const command = new PutObjectCommand(params);
        return this.s3Client.send(command);
    }

    /**
     * Retrieves an object from S3.
     * @param {string} bucket - The name of the S3 bucket.
     * @param {string} key - The key of the object to retrieve.
     * @returns {Promise<import("@aws-sdk/client-s3").GetObjectCommandOutput>} The response from S3 after attempting to retrieve the object.
     */
    async getObject(bucket, key) {
        const params = {
            Bucket: bucket,
            Key: key
        };

        const command = new GetObjectCommand(params);
        return this.s3Client.send(command);
    }

    /**
     * Deletes an object from S3.
     * @param {string} bucket - The name of the S3 bucket.
     * @param {string} key - The key of the object to delete.
     * @returns {Promise<import("@aws-sdk/client-s3").DeleteObjectCommandOutput>} The response from S3 after attempting to delete the object.
     */
    async deleteObject(bucket, key) {
        const params = {
            Bucket: bucket,
            Key: key
        };

        const command = new DeleteObjectCommand(params);
        return this.s3Client.send(command);
    }
}

const serviceInstance = new S3Service();

module.exports = {
    serviceInstance,
    S3Service,
};
