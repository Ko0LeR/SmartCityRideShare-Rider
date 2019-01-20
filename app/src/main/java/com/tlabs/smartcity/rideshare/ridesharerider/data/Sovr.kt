package com.tlabs.smartcity.rideshare.ridesharerider.data

import java.util.*

data class LoginReq(val password:String, val username: String)
data class LoginResp(val token:String)
data class CreateConnOfferResp(val message:CreateConnOfferRespMsg)
data class CreateConnOfferRespMsg(val id: String, val type: String, val message:CreateConnOfferRespMsgMsg)
data class CreateConnOfferRespMsgMsg(
    val did:String,
    val verkey:String,
    val endpoint:String,
    val nonce:String,
    val data: ConOfferData
)
data class PairwiseConnResp(val pairwise: List<Pairwise>)
data class Pairwise(val their_did: String)
data class CreateProofReq(
    val recipientDid: String,
    val proofRequest: ProofRequest
)
data class ProofRequest(
    val name: String = "PhoneNumberVerify",
    val version: String = "1.0",
    val requested_attributes: RequestedAttributes = RequestedAttributes(),
    val requested_predicates: RequestedPredicates2 = RequestedPredicates2()
)
class RequestedPredicates2()
data class RequestedAttributes(
    val attr1_referent: Attr1Referent = Attr1Referent()
)
data class Attr1Referent(
    val name: String = "phone_number@string",
    val restrictions: List<Restriction> = Collections.singletonList(Restriction())
)
data class Restriction(
    val cred_def_id: String = "Gc3HWtzjBuaGyMkSHgomzx:3:CL:18:jesse-credential-def2"
)
data class RequestedPredicates (val id:String,
                                val proof: Proof,
                                val status: String)

data class Proof(val requested_proof:ReqProof)
data class ReqProof(val revealed_attrs:RevAttrs)
data class RevAttrs(val attr1_referent:Attr1Ref)
data class Attr1Ref(val raw:String)
data class AccAndCreProof(val proofRequestId:String)
data class GetProofReqResp(val id:String)

data class CreateConnectionOfferRequest(val meta: Meta, val data: ConOfferData)
data class Meta(val username: String = "jesse")
data class ConOfferData(val app: String = "<your-app-or-service-name>")

data class PayDto(
    val wallet: String = "0x322DDB258B6A596C332A8E50eB18B6Cc3C975AC7",
    val privateKey: String = "f1425f32274e31ea66985c96c9f7a215028a42090129c50bf4bd5875327546f1",
    val driverWallet: String = "0xaff4a042646F6e32F897e6F3C2e310A781606fd5"
)
