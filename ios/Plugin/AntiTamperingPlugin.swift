import Capacitor
import CryptoKit 
import SwiftUI 



@objc(AntiTamperingPlugin)
public class AntiTamperingPlugin: CAPPlugin {



  private func getAssetHashes() -> [String: String]? {
    // Read asset hashes from JSON file within the plugin assets
    guard let filePath = Bundle.main.path(forResource: "assets", ofType: "json",inDirectory: "public") else {
      return nil
    }
    guard let data = try? Data(contentsOf: URL(fileURLWithPath: filePath)) else {
      return nil
    }



    do {
      let json = try JSONSerialization.jsonObject(with: data, options: [])
      guard let dictionary = json as? [String: String] else {
        return nil
      }
     print("Hashes File ",dictionary)
      return dictionary
    } catch {
      print("Error reading asset hashes: \(error)")
      return nil
    }
  }



  @objc func verify(_ call: CAPPluginCall) {
    guard let expectedHashes = getAssetHashes() else {
      call.reject("Failed to read asset hashes")
      return
    }



    var tamperedFiles: [String] = []
    var successCount = 0
      var status:String=""
      var finalVerdict:String=""
      
      



    for (asset, expectedHash) in expectedHashes {
      guard let filePath = Bundle.main.path(forResource: asset.withoutExtension, ofType: asset.pathExtension, inDirectory: "public") else {
        continue
      }



      guard let fileData = try? FileManager.default.contents(atPath: filePath) else {
        tamperedFiles.append(asset)
        continue
      }



     let actualHash = SHA256.hash(data: fileData)
     let actualHashString = Data(bytes: actualHash).hexEncodedString()
     let hashBytes = actualHash.reduce(into: [UInt8]()) { result, byte in  result.append(byte) }
     let hexString = Data(bytes:actualHash)



      if actualHashString != expectedHash {
        tamperedFiles.append(asset)
     finalVerdict = "true"
      }
     else{
         successCount+=1
         finalVerdict="false"
     }
    }



    if tamperedFiles.isEmpty {
          call.resolve(["assetsCount":successCount,"finalVerdict":finalVerdict])
    } else {
     call.resolve(["assetsCount": successCount, "tamperedFiles": tamperedFiles.joined(separator: ", "), "finalVerdict": "true"])}
  }
}

extension Data {
  func hexEncodedString() -> String {
    return map { String(format: "%02x", $0) }.joined()
  }
}
 
extension String {
  var pathExtension: String {
    return (self as NSString).pathExtension ?? ""
  }
}

extension String {
  var withoutExtension: String {
    return (self as NSString).deletingPathExtension ?? self
  }
}
