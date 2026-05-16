// periodApi.js
import axiosClient from "./axiosClient";

const periodApi = {
  getCurrent: () => axiosClient.get("period/current"),
};

export default periodApi;