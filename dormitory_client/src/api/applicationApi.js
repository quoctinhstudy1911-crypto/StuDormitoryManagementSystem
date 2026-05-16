import axiosClient from './axiosClient';

const applicationApi = {
    // Nhận object: { cccd, periodId }
    create: (data) =>
        axiosClient.post('/applications', data),

    getStatus: ({ cccd, periodId }) =>
        axiosClient.get('/applications/status', {
            params: { cccd, periodId }
        }),

    getById: (id) =>
        axiosClient.get(`/applications/${id}`),
};

export default applicationApi;