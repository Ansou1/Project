//
//  ApiMethod.h
//  MSW2
//
//  Created by simon on 29/09/2015.
//  Copyright (c) 2015 Score Lab. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "Reachability.h"

NSString *Id_global;
int       code_global;

@interface ApiMethod : NSObject


-(NSDictionary *) getMethodWithId:(NSString *)id_receive;
-(NSDictionary *) postMethodWithIdentifier:(NSString *)identifiant Password:(NSString *)password;
-(NSDictionary *) postMethodForSuscribeWithUsername:(NSString *)username Firstname:(NSString *)fisrtname Lastname:(NSString *)lastname Email:(NSString *)email Password:(NSString *)password Photo:(NSString *)photo;
-(NSDictionary *) postMethodWithString:(NSString *)data At:(NSString *)url_str;
-(NSDictionary *) getMethodWithString:(NSString *)data;
-(NSDictionary *) putMethodWithString:(NSString *)data At:(NSString *)url_str;
-(NSDictionary *) optionsMethodWithString:(NSString *)data At:(NSString *)url_str;
-(NSDictionary *) deleteMethodWithString:(NSString *)data;
-(NSDictionary *) putMethodWithStringForPhoto:(NSString *)data At:(NSString *)url_str;
- (void) popup:(NSDictionary *) dict;

@end
