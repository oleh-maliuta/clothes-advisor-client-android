const { mockClient } = require('aws-sdk-client-mock');
const { S3Client, PutObjectCommand, GetObjectCommand, DeleteObjectCommand } = require('@aws-sdk/client-s3');
const { S3Service } = require('../services/s3.service');

const s3Mock = mockClient(S3Client);

describe('S3Service', () => {
    const bucket = 'lalala';
    const fileName = 'test-file.png';
    const fileContent = Buffer.from('hello world');
    let s3Service;

    beforeEach(() => {
        s3Mock.reset();
        s3Service = new S3Service({
            region: 'us-east-1',
            accessKeyId: 'test',
            secretAccessKey: 'test',
        });
    });

    describe('putObject', () => {
        it('should successfully upload a file to S3', async () => {
            const eTag = '"12345"';
            s3Mock.on(PutObjectCommand).resolves({ ETag: eTag });

            const result = await s3Service.putObject(bucket, fileContent, fileName, 'image/png');

            expect(result.ETag).toBe(eTag);
            expect(s3Mock.calls()).toHaveLength(1);
            expect(s3Mock.call(0).args[0].input).toMatchObject({
                Bucket: bucket,
                Key: fileName,
                Body: fileContent,
                ContentType: 'image/png'
            });
        });

        it('should throw an error if the S3 upload fails', async () => {
            const errorMessage = 'S3 Upload Failed';
            s3Mock.on(PutObjectCommand).rejects(new Error(errorMessage));

            await expect(s3Service.putObject(bucket, fileContent, fileName))
                .rejects.toThrow(errorMessage);
        });
    });

    describe('getObject', () => {
        it('should retrieve an object from S3', async () => {
            const body = 'mock-stream';
            s3Mock.on(GetObjectCommand).resolves({ Body: body });

            const result = await s3Service.getObject(bucket, fileName);

            expect(result.Body).toBe(body);
            expect(s3Mock.call(0).args[0].input).toMatchObject({
                Bucket: bucket,
                Key: fileName
            });
        });
    });

    describe('deleteObject', () => {
        it('should delete an object from S3', async () => {
            const statusCode = 204;
            s3Mock.on(DeleteObjectCommand)
                .resolves({ $metadata: { httpStatusCode: statusCode } });

            const result = await s3Service.deleteObject(bucket, fileName);

            expect(result.$metadata.httpStatusCode).toBe(statusCode);
            expect(s3Mock.call(0).args[0].input).toEqual({
                Bucket: bucket,
                Key: fileName
            });
        });
    });
});
