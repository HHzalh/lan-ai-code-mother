declare namespace API {
  type AppAddRequest = {
    initPrompt?: string
  }

  type AppAdminUpdateRequest = {
    id?: number
    appName?: string
    cover?: string
    priority?: number
  }

  type AppDeployRequest = {
    appId?: number
  }

  type AppQueryRequest = {
    pageNum?: number
    pageSize?: number
    sortField?: string
    sortOrder?: string
    id?: number
    appName?: string
    cover?: string
    initPrompt?: string
    codeGenType?: string
    deployKey?: string
    priority?: number
    userId?: number
  }

  type AppUpdateRequest = {
    id?: number
    appName?: string
  }

  type AppVO = {
    id?: number
    appName?: string
    cover?: string
    initPrompt?: string
    codeGenType?: string
    deployKey?: string
    deployedTime?: string
    priority?: number
    userId?: number
    createTime?: string
    updateTime?: string
    user?: UserVO
  }

  type BaseResponseAppVO = {
    code?: number
    data?: AppVO
    message?: string
  }

  type BaseResponseBoolean = {
    code?: number
    data?: boolean
    message?: string
  }

  type BaseResponseInteger = {
    code?: number
    data?: number
    message?: string
  }

  type BaseResponseListPointRuleVO = {
    code?: number
    data?: PointRuleVO[]
    message?: string
  }

  type BaseResponseListPointSignInRecordVO = {
    code?: number
    data?: PointSignInRecordVO[]
    message?: string
  }

  type BaseResponseLoginUserVO = {
    code?: number
    data?: LoginUserVO
    message?: string
  }

  type BaseResponseLong = {
    code?: number
    data?: number
    message?: string
  }

  type BaseResponsePageAppVO = {
    code?: number
    data?: PageAppVO
    message?: string
  }

  type BaseResponsePageChatHistory = {
    code?: number
    data?: PageChatHistory
    message?: string
  }

  type BaseResponsePagePointLogVO = {
    code?: number
    data?: PagePointLogVO
    message?: string
  }

  type BaseResponsePageUserAccountVO = {
    code?: number
    data?: PageUserAccountVO
    message?: string
  }

  type BaseResponsePageUserVO = {
    code?: number
    data?: PageUserVO
    message?: string
  }

  type BaseResponsePointSignInResponse = {
    code?: number
    data?: PointSignInResponse
    message?: string
  }

  type BaseResponseString = {
    code?: number
    data?: string
    message?: string
  }

  type BaseResponseUser = {
    code?: number
    data?: User
    message?: string
  }

  type BaseResponseUserAccountVO = {
    code?: number
    data?: UserAccountVO
    message?: string
  }

  type BaseResponseUserVO = {
    code?: number
    data?: UserVO
    message?: string
  }

  type BaseResponseVoid = {
    code?: number
    data?: Record<string, any>
    message?: string
  }

  type canDownloadAppCodeParams = {
    appId: number
  }

  type ChatHistory = {
    id?: number
    message?: string
    messageType?: string
    appId?: number
    userId?: number
    createTime?: string
    updateTime?: string
    isDelete?: number
  }

  type ChatHistoryQueryRequest = {
    pageNum?: number
    pageSize?: number
    sortField?: string
    sortOrder?: string
    id?: number
    message?: string
    messageType?: string
    appId?: number
    userId?: number
    lastCreateTime?: string
  }

  type chatToGenCodeParams = {
    appId: number
    message: string
  }

  type DeleteRequest = {
    id?: number
  }

  type downloadAppCodeParams = {
    appId: number
  }

  type FindPasswordRequest = {
    userAccount?: string
    email?: string
  }

  type getAppVOByIdByAdminParams = {
    id: number
  }

  type getAppVOByIdParams = {
    id: number
  }

  type getSignInCalendarParams = {
    startDate?: string
    endDate?: string
  }

  type getUserByIdParams = {
    id: number
  }

  type getUserVOByIdParams = {
    id: number
  }

  type grantPointsParams = {
    userId: number
    points: number
    remark: string
  }

  type grantPointsToAllParams = {
    points: number
    remark: string
  }

  type handleInvitationCodeParams = {
    invitationCode: string
  }

  type listAppChatHistoryParams = {
    appId: number
    pageSize?: number
    lastCreateTime?: string
  }

  type LoginUserVO = {
    id?: number
    userAccount?: string
    userName?: string
    userAvatar?: string
    userProfile?: string
    userRole?: string
    createTime?: string
    updateTime?: string
  }

  type PageAppVO = {
    records?: AppVO[]
    pageNumber?: number
    pageSize?: number
    totalPage?: number
    totalRow?: number
    optimizeCountQuery?: boolean
  }

  type PageChatHistory = {
    records?: ChatHistory[]
    pageNumber?: number
    pageSize?: number
    totalPage?: number
    totalRow?: number
    optimizeCountQuery?: boolean
  }

  type PagePointLogVO = {
    records?: PointLogVO[]
    pageNumber?: number
    pageSize?: number
    totalPage?: number
    totalRow?: number
    optimizeCountQuery?: boolean
  }

  type PageUserAccountVO = {
    records?: UserAccountVO[]
    pageNumber?: number
    pageSize?: number
    totalPage?: number
    totalRow?: number
    optimizeCountQuery?: boolean
  }

  type PageUserVO = {
    records?: UserVO[]
    pageNumber?: number
    pageSize?: number
    totalPage?: number
    totalRow?: number
    optimizeCountQuery?: boolean
  }

  type PointLogQueryRequest = {
    pageNum?: number
    pageSize?: number
    sortField?: string
    sortOrder?: string
    businessType?: string
    pointType?: string
    startTime?: string
    endTime?: string
    userId?: number
  }

  type PointLogVO = {
    id?: number
    userId?: number
    businessType?: string
    businessTypeText?: string
    businessId?: string
    pointType?: string
    pointTypeText?: string
    pointChange?: number
    beforePoints?: number
    afterPoints?: number
    remark?: string
    createTime?: string
  }

  type PointRuleUpdateRequest = {
    id: number
    ruleKey?: string
    ruleValue?: number
    ruleDesc?: string
    status?: number
  }

  type PointRuleVO = {
    id?: number
    ruleKey?: string
    ruleValue?: number
    ruleDesc?: string
    status?: number
  }

  type PointSignInRecordVO = {
    id?: number
    userId?: number
    signDate?: string
    daysCount?: number
    points?: number
    isBonus?: number
    createTime?: string
  }

  type PointSignInResponse = {
    points?: number
    continuousDays?: number
    isBonus?: boolean
    availablePoints?: number
  }

  type ResetPasswordRequest = {
    userAccount?: string
    email?: string
    code?: string
    newPassword?: string
    checkPassword?: string
  }

  type ServerSentEventString = true

  type serveStaticResourceParams = {
    deployKey: string
  }

  type uploadUserAvatarParams = {
    file: string
  }

  type User = {
    id?: number
    userAccount?: string
    userPassword?: string
    userName?: string
    userAvatar?: string
    userProfile?: string
    userRole?: string
    editTime?: string
    createTime?: string
    updateTime?: string
    isDelete?: number
  }

  type UserAccountQueryRequest = {
    pageNum?: number
    pageSize?: number
    sortField?: string
    sortOrder?: string
    userId?: number
    invitationCode?: string
    minAvailablePoints?: number
    maxAvailablePoints?: number
    minTotalPoints?: number
    maxTotalPoints?: number
    userNickname?: string
  }

  type UserAccountVO = {
    userId?: number
    invitationCode?: string
    totalPoints?: number
    availablePoints?: number
    freezePoints?: number
    totalConsume?: number
    continuousDays?: number
    lastSignDate?: string
    invitationCount?: number
    totalInvitePoints?: number
  }

  type UserAddRequest = {
    userName?: string
    userAccount?: string
    userAvatar?: string
    userProfile?: string
    userRole?: string
  }

  type UserChangePasswordRequest = {
    oldPassword?: string
    newPassword?: string
    checkPassword?: string
  }

  type UserLoginRequest = {
    userAccount?: string
    userPassword?: string
  }

  type UserQueryRequest = {
    pageNum?: number
    pageSize?: number
    sortField?: string
    sortOrder?: string
    id?: number
    userName?: string
    userAccount?: string
    userProfile?: string
    userRole?: string
  }

  type UserRegisterRequest = {
    userAccount?: string
    userPassword?: string
    checkPassword?: string
    invitationCode?: string
  }

  type UserUpdateRequest = {
    id?: number
    userName?: string
    userAvatar?: string
    userProfile?: string
    userRole?: string
  }

  type UserVO = {
    id?: number
    userAccount?: string
    userName?: string
    userAvatar?: string
    userProfile?: string
    userRole?: string
    createTime?: string
  }
}
