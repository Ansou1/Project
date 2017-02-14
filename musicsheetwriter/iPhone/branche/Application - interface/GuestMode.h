//
//  GuestMode.h
//  MSW
//
//  Created by simon on 28/01/2016.
//  Copyright (c) 2016 Score Lab. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "ApiMethod.h"

@interface GuestMode : NSObject

-(BOOL) CheckIfTheUserIsAGuestId:(id)object;
-(void) IfTheUserIsAGuest:(id)object;

@end
