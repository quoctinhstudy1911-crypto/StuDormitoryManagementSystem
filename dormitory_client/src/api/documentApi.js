import axiosClient from './axiosClient';

const documentApi = {
  upload: (appId, type, fileUrl) =>
    axiosClient.post('/documents', {
      applicationId: appId,
      type,
      fileUrl
    }),

  getByApplication: (appId) =>
    axiosClient.get(`/documents/application/${appId}`),

  verify: (docId, status) =>
    axiosClient.put(`/documents/${docId}/verify`, null, {
      params: { status }
    }),
};

export default documentApi;

